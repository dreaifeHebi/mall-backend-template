package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 订单统计DTO
 * Created by mall on 2025/06/22.
 */
public class OrderStatisticsDto {
    @ApiModelProperty(value = "日期", example = "2025-06-22")
    private String date;

    @ApiModelProperty(value = "订单数量")
    private Integer orderCount;

    @ApiModelProperty(value = "订单金额(分)")
    private Long orderAmount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Long getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Long orderAmount) {
        this.orderAmount = orderAmount;
    }
}
