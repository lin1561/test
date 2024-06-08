package org.example.controller;

import com.alibaba.fastjson.JSONObject;
import org.example.entity.Commodity;
import org.example.service.ShoppingService;
import org.example.service.impl.ShoppingServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import org.example.service.Result;
@RestController
public class ShoppingController {
    private final ShoppingService shoppingService;
    public ShoppingController(ShoppingService shoppingService){
        this.shoppingService = shoppingService;
    }

    @GetMapping("/list")
    public Result getAllCommodity() {
        List<Commodity> list = shoppingService.findAll();
        return Result.ok().data("commodityList", list);
    }

    @GetMapping("/findById")
    public Result findById(@RequestParam("id") Long id) {
        Commodity commodity = shoppingService.findById(id);
        return Result.ok().data("comodityList", commodity);
    }
    @PostMapping("/buy")
    public Result buy(@RequestBody JSONObject request){
        Long commodityId = request.getLong("commodityId");
        BigDecimal money = request.getBigDecimal("money");
        String phone = request.getString("phone");
        if(shoppingService.buy(commodityId, money, phone)){
            return Result.ok();
        } else {
            return Result.error();
        }
    }
}
