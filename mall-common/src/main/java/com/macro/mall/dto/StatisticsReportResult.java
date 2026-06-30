package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 统计报表结果
 */
public class StatisticsReportResult {
    
    @ApiModelProperty(value = "今日统计")
    private DailyStatistics todayStats;
    
    @ApiModelProperty(value = "昨日统计")
    private DailyStatistics yesterdayStats;
    
    @ApiModelProperty(value = "本月统计")
    private MonthlyStatistics monthlyStats;
    
    @ApiModelProperty(value = "近30天趋势")
    private List<DailyTrend> trends;
    
    @ApiModelProperty(value = "销售排行")
    private List<SalesRank> salesRanks;
    
    @ApiModelProperty(value = "地区分布")
    private Map<String, Integer> regionDistribution;
    
    public static class DailyStatistics {
        @ApiModelProperty(value = "订单数量")
        private Integer orderCount;
        
        @ApiModelProperty(value = "订单金额")
        private BigDecimal orderAmount;
        
        @ApiModelProperty(value = "支付订单数")
        private Integer paidOrderCount;
        
        @ApiModelProperty(value = "新增用户数")
        private Integer newUserCount;
        
        @ApiModelProperty(value = "活跃用户数")
        private Integer activeUserCount;
        
        @ApiModelProperty(value = "新增商品数")
        private Integer newProductCount;
        
        // getters and setters
        public Integer getOrderCount() {
            return orderCount;
        }
        
        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }
        
        public BigDecimal getOrderAmount() {
            return orderAmount;
        }
        
        public void setOrderAmount(BigDecimal orderAmount) {
            this.orderAmount = orderAmount;
        }
        
        public Integer getPaidOrderCount() {
            return paidOrderCount;
        }
        
        public void setPaidOrderCount(Integer paidOrderCount) {
            this.paidOrderCount = paidOrderCount;
        }
        
        public Integer getNewUserCount() {
            return newUserCount;
        }
        
        public void setNewUserCount(Integer newUserCount) {
            this.newUserCount = newUserCount;
        }
        
        public Integer getActiveUserCount() {
            return activeUserCount;
        }
        
        public void setActiveUserCount(Integer activeUserCount) {
            this.activeUserCount = activeUserCount;
        }
        
        public Integer getNewProductCount() {
            return newProductCount;
        }
        
        public void setNewProductCount(Integer newProductCount) {
            this.newProductCount = newProductCount;
        }
    }
    
    public static class MonthlyStatistics {
        @ApiModelProperty(value = "月份")
        private String month;
        
        @ApiModelProperty(value = "总订单数")
        private Integer totalOrders;
        
        @ApiModelProperty(value = "总金额")
        private BigDecimal totalAmount;
        
        @ApiModelProperty(value = "新增用户数")
        private Integer newUsers;
        
        @ApiModelProperty(value = "月增长率")
        private BigDecimal growthRate;
        
        // getters and setters
        public String getMonth() {
            return month;
        }
        
        public void setMonth(String month) {
            this.month = month;
        }
        
        public Integer getTotalOrders() {
            return totalOrders;
        }
        
        public void setTotalOrders(Integer totalOrders) {
            this.totalOrders = totalOrders;
        }
        
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
        
        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }
        
        public Integer getNewUsers() {
            return newUsers;
        }
        
        public void setNewUsers(Integer newUsers) {
            this.newUsers = newUsers;
        }
        
        public BigDecimal getGrowthRate() {
            return growthRate;
        }
        
        public void setGrowthRate(BigDecimal growthRate) {
            this.growthRate = growthRate;
        }
    }
    
    public static class DailyTrend {
        @ApiModelProperty(value = "日期")
        private String date;
        
        @ApiModelProperty(value = "订单数量")
        private Integer orderCount;
        
        @ApiModelProperty(value = "订单金额")
        private BigDecimal orderAmount;
        
        @ApiModelProperty(value = "用户数量")
        private Integer userCount;
        
        // getters and setters
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
        
        public BigDecimal getOrderAmount() {
            return orderAmount;
        }
        
        public void setOrderAmount(BigDecimal orderAmount) {
            this.orderAmount = orderAmount;
        }
        
        public Integer getUserCount() {
            return userCount;
        }
        
        public void setUserCount(Integer userCount) {
            this.userCount = userCount;
        }
    }
    
    public static class SalesRank {
        @ApiModelProperty(value = "商品ID")
        private Long productId;
        
        @ApiModelProperty(value = "商品名称")
        private String productName;
        
        @ApiModelProperty(value = "销售数量")
        private Integer salesCount;
        
        @ApiModelProperty(value = "销售金额")
        private BigDecimal salesAmount;
        
        @ApiModelProperty(value = "排名")
        private Integer rank;
        
        // getters and setters
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
        
        public Integer getSalesCount() {
            return salesCount;
        }
        
        public void setSalesCount(Integer salesCount) {
            this.salesCount = salesCount;
        }
        
        public BigDecimal getSalesAmount() {
            return salesAmount;
        }
        
        public void setSalesAmount(BigDecimal salesAmount) {
            this.salesAmount = salesAmount;
        }
        
        public Integer getRank() {
            return rank;
        }
        
        public void setRank(Integer rank) {
            this.rank = rank;
        }
    }
    
    // getters and setters for main class
    public DailyStatistics getTodayStats() {
        return todayStats;
    }
    
    public void setTodayStats(DailyStatistics todayStats) {
        this.todayStats = todayStats;
    }
    
    public DailyStatistics getYesterdayStats() {
        return yesterdayStats;
    }
    
    public void setYesterdayStats(DailyStatistics yesterdayStats) {
        this.yesterdayStats = yesterdayStats;
    }
    
    public MonthlyStatistics getMonthlyStats() {
        return monthlyStats;
    }
    
    public void setMonthlyStats(MonthlyStatistics monthlyStats) {
        this.monthlyStats = monthlyStats;
    }
    
    public List<DailyTrend> getTrends() {
        return trends;
    }
    
    public void setTrends(List<DailyTrend> trends) {
        this.trends = trends;
    }
    
    public List<SalesRank> getSalesRanks() {
        return salesRanks;
    }
    
    public void setSalesRanks(List<SalesRank> salesRanks) {
        this.salesRanks = salesRanks;
    }
    
    public Map<String, Integer> getRegionDistribution() {
        return regionDistribution;
    }
    
    public void setRegionDistribution(Map<String, Integer> regionDistribution) {
        this.regionDistribution = regionDistribution;
    }
}
