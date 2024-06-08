package org.example.service.impl;

import org.example.entity.Commodity;
import org.example.mapper.CommodityMapper;
import org.example.service.ShoppingService;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class ShoppingServiceImpl implements ShoppingService {
    //使用StringRedisTemplate完成对Redis的调用
    private final StringRedisTemplate stringRedisTemplate;
    private final CommodityMapper commodityMapper;
    private final DefaultRedisScript redisScript;
    public ShoppingServiceImpl(StringRedisTemplate stringRedisTemplate, CommodityMapper commodityMapper){
        this.stringRedisTemplate = stringRedisTemplate;
        this.commodityMapper = commodityMapper;
        //创建默认脚本
        redisScript = new DefaultRedisScript<>();
        //表示redisScript的结果类型将被设置为布尔类型
        redisScript.setResultType(Boolean.class);
        //编写脚本，整个脚本作为一个整体执行，实现原子性，防止高并发状态下可能造成的库存数量不足仍旧卖出商品的超卖现象
        //先判断数量，不足则失败，否则调用 Redis 的 HINCRBY 命令，将指定哈希表（KEYS[1]）中的 'stock' 字段值减少 1
        String script = "if tonumber(redis.call('HGET', KEYS[1], 'stock')) <=0 then return false; " +
                "else redis.call('HINCRBY', KEYS[1], 'stock', -1); return true; " +
                "end";
        redisScript.setScriptText(script);
    }

/*    查找前先访问redis是否有缓存的数据，避免直接访问数据库，将硬盘操作转为内存操作
    充分发挥redis作为缓存中间件的作用*/
    @Override
    public List<Commodity> findAll() {
        //此处通过boundSetOps方法对键进行绑定，不需要每次调用都输入键名
        BoundSetOperations<String, String> boundSetOperations =
                stringRedisTemplate.boundSetOps("commodity");
        //返回一个实现了 HashOperations 接口的对象。该对象用于操作 Redis 中的哈希数据类型
        HashOperations<String, String, String> hashOperations =
                stringRedisTemplate.opsForHash();
        Set<String> commoditySet = boundSetOperations.members();
        List<Commodity> commodityList;
        if(commoditySet == null || commoditySet.isEmpty()) {
            commodityList = commodityMapper.findAll();
            for (Commodity commodity : commodityList) {
                String id = String.valueOf(commodity.getId());
                boundSetOperations.add(id);
                hashOperations.putIfAbsent("commodity: " + id, "title", commodity.getTitle());
                hashOperations.putIfAbsent("commodity: " + id, "price", commodity.getPrice().toString());
                hashOperations.putIfAbsent("commodity: " + id, "stock", String.valueOf(commodity.getStock()));


            }
        } else {
            commodityList = new ArrayList<>();
            Commodity commodity;
            for (String id : commoditySet) {
                String title = hashOperations.get("commodity: " + id, "title");
                String price = hashOperations.get("commodity: " + id, "price");
                String stock = hashOperations.get("commodity: " + id, "stock");
                if (title == null || price ==null || stock == null) {
                    commodity = commodityMapper.findById(Long.parseLong(id));
                    hashOperations.putIfAbsent("commodity: " + id, "title", commodity.getTitle());
                    hashOperations.putIfAbsent("commodity: " + id, "price", commodity.getPrice().toString());
                    hashOperations.putIfAbsent("commodity: " + id, "stock", String.valueOf(commodity.getStock()));
                } else {
                    commodity = new Commodity(Long.parseLong(id), title, new BigDecimal(price), Long.parseLong(stock));
                }
                commodityList.add(commodity);
            }
        }
        return commodityList;
    }

    @Override
    public Commodity findById(Long commodityId) {
        Commodity commodity;
        BoundHashOperations<String, String, String> boundHashOperations =
                stringRedisTemplate.boundHashOps("commodity: " + commodityId);
        String title = boundHashOperations.get("title");
        String price = boundHashOperations.get("price");
        String stock = boundHashOperations.get("stock");
        if(title == null || price == null || stock ==null) {
            commodity = commodityMapper.findById(commodityId);
            boundHashOperations.putIfAbsent("title", commodity.getTitle());
            boundHashOperations.putIfAbsent("price", commodity.getPrice().toString());
            boundHashOperations.putIfAbsent("stock", String.valueOf(commodity.getStock()));
        } else {
            commodity = new Commodity(commodityId, title, new BigDecimal(price), Long.parseLong(stock));
        }
        return commodity;
    }

    @Override
    public Boolean buy(Long commodityId, BigDecimal money, String phone) {
        BoundHashOperations<String, String, String> boundHashOperations = stringRedisTemplate.boundHashOps("commodity: " + commodityId);
        String stockString = boundHashOperations.get("stock");
        String priceString = boundHashOperations.get("price");
        BigDecimal price;
        long stock;
        if (stockString == null){
            Commodity commodity = commodityMapper.findById(commodityId);
            stock = commodity.getStock();
            boundHashOperations.putIfAbsent("title", commodity.getTitle());
            boundHashOperations.putIfAbsent("price", commodity.getPrice().toString());
            boundHashOperations.putIfAbsent("stock", String.valueOf(commodity.getStock()));
        } else {
            stock = Long.parseLong(stockString);
        }

        price = new BigDecimal(priceString);//此处比较付款金额与商品价格，若小则不可进行购买
        if(money.compareTo(price) == -1) {
            return false;
        }

        if (stock <=0 ) {
            return false;
        } else {
            Boolean result = (Boolean) stringRedisTemplate.execute(redisScript, Collections.singletonList("commodity: " + commodityId));
            if (result == null || !result){
                return false;
            }
            //将消息发送到消息队列。
            stringRedisTemplate.convertAndSend("mq", "{" +
                    "commodityId:" + commodityId +
                    ", money:" + money +
                    ", phone:'" + phone + "\'" +
                    '}');
            return true;
        }
    }

}
