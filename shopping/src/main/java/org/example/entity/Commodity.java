package org.example.entity;

import java.math.BigDecimal;

public class Commodity {
    private final long id;
    private final String title;
    private final BigDecimal price;
    private final long stock;
    public Commodity(long id, String title, BigDecimal price, long stock){
        this.id=id;
        this.title=title;
        this.price=price;
        this.stock=stock;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long getStock() {
        return stock;
    }

    public String getTitle() {
        return title;
    }
}
