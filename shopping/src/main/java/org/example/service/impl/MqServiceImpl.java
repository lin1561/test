package org.example.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.mapper.CommodityMapper;
import org.example.mapper.OrderMapper;
import org.example.service.MqService;
import org.example.service.OrderDto;
import org.springframework.stereotype.Service;

@Service
public class MqServiceImpl implements MqService {
    private final CommodityMapper commodityMapper;
    private final OrderMapper orderMapper;
    public MqServiceImpl(CommodityMapper commodityMapper, OrderMapper orderMapper) {
        this.commodityMapper = commodityMapper;
        this.orderMapper = orderMapper;
    }

    //收到消息后对商品进行解析得到id、金额、电话等信息，后对商品执行减库存和写订单等操作
    @Override
    public void receiveMessage(String message) {
        //将Json格式字符串解析为对象
        OrderDto orderDto = JSON.parseObject(message, OrderDto.class);
        commodityMapper.reduceStock(orderDto.getCommodityId());
        orderMapper.insert(orderDto.getCommodityId(), orderDto.getMoney(), orderDto.getPhone());
    }

}