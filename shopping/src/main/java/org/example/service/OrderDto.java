package org.example.service;

import java.math.BigDecimal;

public class OrderDto {

    private final long commodityId;
    private final BigDecimal money;
    private final String phone;

    public OrderDto(long commodityId, BigDecimal money, String phone) {
        this.commodityId = commodityId;
        this.money = money;
        this.phone = phone;
    }

    public long getCommodityId() {
        return commodityId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public String getPhone() {
        return phone;
    }
}
