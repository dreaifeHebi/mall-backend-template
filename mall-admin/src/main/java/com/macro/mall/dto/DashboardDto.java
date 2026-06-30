package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 仪表盘数据DTO
 * Created by mall on 2025/06/22.
 */
public class DashboardDto {
    @ApiModelProperty(value = "今日订单数")
    private Integer todayOrderCount;

    @ApiModelProperty(value = "今日订单金额(分)")
    private Long todayOrderAmount;

    @ApiModelProperty(value = "总用户数")
    private Integer totalUserCount;

    @ApiModelProperty(value = "总商品数")
    private Integer totalProductCount;

    @ApiModelProperty(value = "本月订单数")
    private Integer monthOrderCount;

    @ApiModelProperty(value = "本月订单金额(分)")
    private Long monthOrderAmount;

    @ApiModelProperty(value = "待付款订单数")
    private Integer pendingPaymentCount;

    @ApiModelProperty(value = "已完成订单数")
    private Integer completedOrderCount;

    @ApiModelProperty(value = "待收货订单数")
    private Integer pendingReceiveCount;

    @ApiModelProperty(value = "待发货订单数")
    private Integer pendingShipmentCount;

    @ApiModelProperty(value = "缺货商品数")
    private Integer stockOutCount;

    @ApiModelProperty(value = "待退款订单数")
    private Integer pendingRefundCount;

    @ApiModelProperty(value = "已发货订单数")
    private Integer shippedOrderCount;

    @ApiModelProperty(value = "待退货订单数")
    private Integer pendingReturnCount;

    @ApiModelProperty(value = "下架商品数")
    private Integer productOfflineCount;

    @ApiModelProperty(value = "上架商品数")
    private Integer productOnlineCount;

    @ApiModelProperty(value = "库存不足商品数")
    private Integer productLowStockCount;

    @ApiModelProperty(value = "今日新增用户数")
    private Integer todayNewUserCount;

    @ApiModelProperty(value = "昨日新增用户数")
    private Integer yesterdayNewUserCount;

    @ApiModelProperty(value = "本月新增用户数")
    private Integer monthNewUserCount;

    @ApiModelProperty(value = "本周订单数")
    private Integer weekOrderCount;

    @ApiModelProperty(value = "本周订单金额(分)")
    private Long weekOrderAmount;

    @ApiModelProperty(value = "本月订单增长率(%)")
    private Double monthOrderGrowth;

    @ApiModelProperty(value = "本周订单增长率(%)")
    private Double weekOrderGrowth;

    @ApiModelProperty(value = "本月金额增长率(%)")
    private Double monthAmountGrowth;

    @ApiModelProperty(value = "本周金额增长率(%)")
    private Double weekAmountGrowth;

    // Getters and Setters
    public Integer getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(Integer todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }

    public Long getTodayOrderAmount() {
        return todayOrderAmount;
    }

    public void setTodayOrderAmount(Long todayOrderAmount) {
        this.todayOrderAmount = todayOrderAmount;
    }

    public Integer getTotalUserCount() {
        return totalUserCount;
    }

    public void setTotalUserCount(Integer totalUserCount) {
        this.totalUserCount = totalUserCount;
    }

    public Integer getTotalProductCount() {
        return totalProductCount;
    }

    public void setTotalProductCount(Integer totalProductCount) {
        this.totalProductCount = totalProductCount;
    }

    public Integer getMonthOrderCount() {
        return monthOrderCount;
    }

    public void setMonthOrderCount(Integer monthOrderCount) {
        this.monthOrderCount = monthOrderCount;
    }

    public Long getMonthOrderAmount() {
        return monthOrderAmount;
    }

    public void setMonthOrderAmount(Long monthOrderAmount) {
        this.monthOrderAmount = monthOrderAmount;
    }

    public Integer getPendingPaymentCount() {
        return pendingPaymentCount;
    }

    public void setPendingPaymentCount(Integer pendingPaymentCount) {
        this.pendingPaymentCount = pendingPaymentCount;
    }

    public Integer getCompletedOrderCount() {
        return completedOrderCount;
    }

    public void setCompletedOrderCount(Integer completedOrderCount) {
        this.completedOrderCount = completedOrderCount;
    }

    public Integer getPendingReceiveCount() {
        return pendingReceiveCount;
    }

    public void setPendingReceiveCount(Integer pendingReceiveCount) {
        this.pendingReceiveCount = pendingReceiveCount;
    }

    public Integer getPendingShipmentCount() {
        return pendingShipmentCount;
    }

    public void setPendingShipmentCount(Integer pendingShipmentCount) {
        this.pendingShipmentCount = pendingShipmentCount;
    }

    public Integer getStockOutCount() {
        return stockOutCount;
    }

    public void setStockOutCount(Integer stockOutCount) {
        this.stockOutCount = stockOutCount;
    }

    public Integer getPendingRefundCount() {
        return pendingRefundCount;
    }

    public void setPendingRefundCount(Integer pendingRefundCount) {
        this.pendingRefundCount = pendingRefundCount;
    }

    public Integer getShippedOrderCount() {
        return shippedOrderCount;
    }

    public void setShippedOrderCount(Integer shippedOrderCount) {
        this.shippedOrderCount = shippedOrderCount;
    }

    public Integer getPendingReturnCount() {
        return pendingReturnCount;
    }

    public void setPendingReturnCount(Integer pendingReturnCount) {
        this.pendingReturnCount = pendingReturnCount;
    }

    public Integer getProductOfflineCount() {
        return productOfflineCount;
    }

    public void setProductOfflineCount(Integer productOfflineCount) {
        this.productOfflineCount = productOfflineCount;
    }

    public Integer getProductOnlineCount() {
        return productOnlineCount;
    }

    public void setProductOnlineCount(Integer productOnlineCount) {
        this.productOnlineCount = productOnlineCount;
    }

    public Integer getProductLowStockCount() {
        return productLowStockCount;
    }

    public void setProductLowStockCount(Integer productLowStockCount) {
        this.productLowStockCount = productLowStockCount;
    }

    public Integer getTodayNewUserCount() {
        return todayNewUserCount;
    }

    public void setTodayNewUserCount(Integer todayNewUserCount) {
        this.todayNewUserCount = todayNewUserCount;
    }

    public Integer getYesterdayNewUserCount() {
        return yesterdayNewUserCount;
    }

    public void setYesterdayNewUserCount(Integer yesterdayNewUserCount) {
        this.yesterdayNewUserCount = yesterdayNewUserCount;
    }

    public Integer getMonthNewUserCount() {
        return monthNewUserCount;
    }

    public void setMonthNewUserCount(Integer monthNewUserCount) {
        this.monthNewUserCount = monthNewUserCount;
    }

    public Integer getWeekOrderCount() {
        return weekOrderCount;
    }

    public void setWeekOrderCount(Integer weekOrderCount) {
        this.weekOrderCount = weekOrderCount;
    }

    public Long getWeekOrderAmount() {
        return weekOrderAmount;
    }

    public void setWeekOrderAmount(Long weekOrderAmount) {
        this.weekOrderAmount = weekOrderAmount;
    }

    public Double getMonthOrderGrowth() {
        return monthOrderGrowth;
    }

    public void setMonthOrderGrowth(Double monthOrderGrowth) {
        this.monthOrderGrowth = monthOrderGrowth;
    }

    public Double getWeekOrderGrowth() {
        return weekOrderGrowth;
    }

    public void setWeekOrderGrowth(Double weekOrderGrowth) {
        this.weekOrderGrowth = weekOrderGrowth;
    }

    public Double getMonthAmountGrowth() {
        return monthAmountGrowth;
    }

    public void setMonthAmountGrowth(Double monthAmountGrowth) {
        this.monthAmountGrowth = monthAmountGrowth;
    }

    public Double getWeekAmountGrowth() {
        return weekAmountGrowth;
    }

    public void setWeekAmountGrowth(Double weekAmountGrowth) {
        this.weekAmountGrowth = weekAmountGrowth;
    }
}
