package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 管理主页统计数据
 * Created by mall on 2024/06/22.
 */
public class AdminDashboardResult {

    @ApiModelProperty(value = "用户总数")
    private Long totalUsers;

    @ApiModelProperty(value = "订单总数")
    private Long totalOrders;

    @ApiModelProperty(value = "今日新增用户")
    private Long todayNewUsers;

    @ApiModelProperty(value = "今日销售额")
    private BigDecimal todaySales;

    @ApiModelProperty(value = "本月销售额")
    private BigDecimal monthSales;

    @ApiModelProperty(value = "待发货订单数")
    private Long pendingOrders;

    @ApiModelProperty(value = "销售趋势图数据（最近7天）")
    private List<SalesTrendItem> salesTrend;

    @ApiModelProperty(value = "订单状态分布")
    private Map<String, Long> orderStatusDistribution;    @ApiModelProperty(value = "热销商品Top10")
    private List<HotProductItem> hotProducts;

    @ApiModelProperty(value = "总销售额")
    private BigDecimal totalSales;

    @ApiModelProperty(value = "待付款订单数")
    private Long waitingPayOrders;

    @ApiModelProperty(value = "待发货订单数")
    private Long waitingDeliveryOrders;

    @ApiModelProperty(value = "新增用户数")
    private Long newMembers;

    @ApiModelProperty(value = "商品总数")
    private Long totalProducts;

    @ApiModelProperty(value = "低库存商品数")
    private Long lowStockProducts;

    @ApiModelProperty(value = "日期列表")
    private List<String> dates;

    @ApiModelProperty(value = "销售数据")
    private List<BigDecimal> salesData;

    @ApiModelProperty(value = "订单数据")
    private List<Long> orderData;

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTodayNewUsers() {
        return todayNewUsers;
    }

    public void setTodayNewUsers(Long todayNewUsers) {
        this.todayNewUsers = todayNewUsers;
    }

    public BigDecimal getTodaySales() {
        return todaySales;
    }

    public void setTodaySales(BigDecimal todaySales) {
        this.todaySales = todaySales;
    }

    public BigDecimal getMonthSales() {
        return monthSales;
    }

    public void setMonthSales(BigDecimal monthSales) {
        this.monthSales = monthSales;
    }

    public Long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(Long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public List<SalesTrendItem> getSalesTrend() {
        return salesTrend;
    }

    public void setSalesTrend(List<SalesTrendItem> salesTrend) {
        this.salesTrend = salesTrend;
    }

    public Map<String, Long> getOrderStatusDistribution() {
        return orderStatusDistribution;
    }

    public void setOrderStatusDistribution(Map<String, Long> orderStatusDistribution) {
        this.orderStatusDistribution = orderStatusDistribution;
    }

    public List<HotProductItem> getHotProducts() {
        return hotProducts;
    }    public void setHotProducts(List<HotProductItem> hotProducts) {
        this.hotProducts = hotProducts;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public Long getWaitingPayOrders() {
        return waitingPayOrders;
    }

    public void setWaitingPayOrders(Long waitingPayOrders) {
        this.waitingPayOrders = waitingPayOrders;
    }

    public Long getWaitingDeliveryOrders() {
        return waitingDeliveryOrders;
    }

    public void setWaitingDeliveryOrders(Long waitingDeliveryOrders) {
        this.waitingDeliveryOrders = waitingDeliveryOrders;
    }

    public Long getNewMembers() {
        return newMembers;
    }

    public void setNewMembers(Long newMembers) {
        this.newMembers = newMembers;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(Long lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<BigDecimal> getSalesData() {
        return salesData;
    }

    public void setSalesData(List<BigDecimal> salesData) {
        this.salesData = salesData;
    }

    public List<Long> getOrderData() {
        return orderData;
    }

    public void setOrderData(List<Long> orderData) {
        this.orderData = orderData;
    }

    /**
     * 销售趋势项
     */
    public static class SalesTrendItem {
        @ApiModelProperty(value = "日期")
        private String date;

        @ApiModelProperty(value = "销售额")
        private BigDecimal amount;

        @ApiModelProperty(value = "订单数")
        private Long orderCount;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public Long getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Long orderCount) {
            this.orderCount = orderCount;
        }
    }

    /**
     * 热销商品项
     */
    public static class HotProductItem {
        @ApiModelProperty(value = "商品ID")
        private Long productId;

        @ApiModelProperty(value = "商品名称")
        private String productName;

        @ApiModelProperty(value = "销售数量")
        private Long saleCount;

        @ApiModelProperty(value = "销售额")
        private BigDecimal saleAmount;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Long getSaleCount() {
            return saleCount;
        }

        public void setSaleCount(Long saleCount) {
            this.saleCount = saleCount;
        }

        public BigDecimal getSaleAmount() {
            return saleAmount;
        }

        public void setSaleAmount(BigDecimal saleAmount) {
            this.saleAmount = saleAmount;
        }
    }
}
