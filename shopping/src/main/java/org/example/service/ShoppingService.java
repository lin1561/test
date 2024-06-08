package org.example.service;
import org.example.controller.ShoppingController;
import org.example.entity.Commodity;

import java.math.BigDecimal;
import java.util.List;

public interface ShoppingService {
    List<Commodity> findAll();
    Commodity findById(Long commodityId);
    Boolean buy(Long commodityId, BigDecimal money, String phone);

}
