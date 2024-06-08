package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface OrderMapper {
    void insert(@Param("commodityId")long commodityId, @Param("money") BigDecimal money, @Param("phone") String Phone);
}
