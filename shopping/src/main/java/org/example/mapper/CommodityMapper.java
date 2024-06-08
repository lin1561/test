package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.entity.Commodity;

import java.util.List;

@Mapper
public interface CommodityMapper {
    List<Commodity> findAll();
    Commodity findById(long commodityId);
    void reduceStock(long commodityId);
}
