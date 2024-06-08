package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    private long id;
    private BigDecimal money;
    private long phone;
    private long CommodityId;
/*    @JsonFormat 默认是标准时区的时间，多出现少8小时的情况
    使用时，按需求加上时区 北京时间 东八区 timezone=”GMT+8”
    作用：后台的时间 格式化 发送到前台

    @DateTimeFormat 接受前台的时间格式 传到后台的格式*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
