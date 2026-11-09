package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 管理员首页统计结果DTO
 * Created by mall on 2024/06/22.
 */
public class AdminDashboardResult {
    @ApiModelProperty(value = "今日订单数")
    private Integer todayOrderCount;

    @ApiModelProperty(value = "今日销售额")
    private BigDecimal todaySalesAmount;

    @ApiModelProperty(value = "今日新增用户")
    private Integer todayUserCount;

    @ApiModelProperty(value = "今日商品访问量")
    private Integer todayProductViewCount;

    @ApiModelProperty(value = "本月订单数")
    private Integer monthOrderCount;

    @ApiModelProperty(value = "本月销售额")
    private BigDecimal monthSalesAmount;

    @ApiModelProperty(value = "本月新增用户")
    private Integer monthUserCount;

    @ApiModelProperty(value = "本月商品访问量")
    private Integer monthProductViewCount;

    @ApiModelProperty(value = "待发货订单数")
    private Integer pendingOrderCount;

    @ApiModelProperty(value = "库存不足商品数")
    private Integer lowStockProductCount;

    @ApiModelProperty(value = "热销商品数")
    private Integer hotProductCount;

    @ApiModelProperty(value = "活跃用户数")
    private Integer activeUserCount;

    public Integer getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(Integer todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }

    public BigDecimal getTodaySalesAmount() {
        return todaySalesAmount;
    }

    public void setTodaySalesAmount(BigDecimal todaySalesAmount) {
        this.todaySalesAmount = todaySalesAmount;
    }

    public Integer getTodayUserCount() {
        return todayUserCount;
    }

    public void setTodayUserCount(Integer todayUserCount) {
        this.todayUserCount = todayUserCount;
    }

    public Integer getTodayProductViewCount() {
        return todayProductViewCount;
    }

    public void setTodayProductViewCount(Integer todayProductViewCount) {
        this.todayProductViewCount = todayProductViewCount;
    }

    public Integer getMonthOrderCount() {
        return monthOrderCount;
    }

    public void setMonthOrderCount(Integer monthOrderCount) {
        this.monthOrderCount = monthOrderCount;
    }

    public BigDecimal getMonthSalesAmount() {
        return monthSalesAmount;
    }

    public void setMonthSalesAmount(BigDecimal monthSalesAmount) {
        this.monthSalesAmount = monthSalesAmount;
    }

    public Integer getMonthUserCount() {
        return monthUserCount;
    }

    public void setMonthUserCount(Integer monthUserCount) {
        this.monthUserCount = monthUserCount;
    }

    public Integer getMonthProductViewCount() {
        return monthProductViewCount;
    }

    public void setMonthProductViewCount(Integer monthProductViewCount) {
        this.monthProductViewCount = monthProductViewCount;
    }

    public Integer getPendingOrderCount() {
        return pendingOrderCount;
    }

    public void setPendingOrderCount(Integer pendingOrderCount) {
        this.pendingOrderCount = pendingOrderCount;
    }

    public Integer getLowStockProductCount() {
        return lowStockProductCount;
    }

    public void setLowStockProductCount(Integer lowStockProductCount) {
        this.lowStockProductCount = lowStockProductCount;
    }

    public Integer getHotProductCount() {
        return hotProductCount;
    }

    public void setHotProductCount(Integer hotProductCount) {
        this.hotProductCount = hotProductCount;
    }

    public Integer getActiveUserCount() {
        return activeUserCount;
    }    public void setActiveUserCount(Integer activeUserCount) {
        this.activeUserCount = activeUserCount;
    }

    // 兼容旧方法
    public void setTotalOrders(long totalOrders) {
        this.todayOrderCount = (int) totalOrders;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.todaySalesAmount = totalSales;
    }

    public void setWaitingPayOrders(long waitingPayOrders) {
        this.pendingOrderCount = (int) waitingPayOrders;
    }

    public void setWaitingDeliveryOrders(long waitingDeliveryOrders) {
        this.pendingOrderCount = (int) waitingDeliveryOrders;
    }

    public void setNewMembers(long newMembers) {
        this.todayUserCount = (int) newMembers;
    }

    public void setTotalProducts(long totalProducts) {
        this.hotProductCount = (int) totalProducts;
    }

    public void setLowStockProducts(long lowStockProducts) {
        this.lowStockProductCount = (int) lowStockProducts;
    }

    public void setDates(java.util.List<String> dates) {
        // 暂时忽略
    }

    public void setSalesData(java.util.List<BigDecimal> salesData) {
        // 暂时忽略
    }

    public void setOrderData(java.util.List<Long> orderData) {
        // 暂时忽略
    }
}
