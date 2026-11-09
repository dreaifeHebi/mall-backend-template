package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 统计报表结果DTO
 * Created by mall on 2024/06/22.
 */
public class StatisticsReportResult {
    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "订单数量")
    private Integer orderCount;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "用户数量")
    private Integer userCount;

    @ApiModelProperty(value = "商品访问量")
    private Integer productViewCount;

    @ApiModelProperty(value = "下单转化率")
    private BigDecimal orderConversionRate;

    @ApiModelProperty(value = "客单价")
    private BigDecimal avgOrderAmount;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public Integer getProductViewCount() {
        return productViewCount;
    }

    public void setProductViewCount(Integer productViewCount) {
        this.productViewCount = productViewCount;
    }

    public BigDecimal getOrderConversionRate() {
        return orderConversionRate;
    }

    public void setOrderConversionRate(BigDecimal orderConversionRate) {
        this.orderConversionRate = orderConversionRate;
    }

    public BigDecimal getAvgOrderAmount() {
        return avgOrderAmount;
    }

    public void setAvgOrderAmount(BigDecimal avgOrderAmount) {
        this.avgOrderAmount = avgOrderAmount;
    }

    // 静态内部类，用于构建返回对象
    public static class Builder {
        private Date date;
        private Integer orderCount;
        private BigDecimal orderAmount;
        private Integer userCount;
        private Integer productViewCount;
        private BigDecimal orderConversionRate;
        private BigDecimal avgOrderAmount;

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder orderCount(Integer orderCount) {
            this.orderCount = orderCount;
            return this;
        }

        public Builder orderAmount(BigDecimal orderAmount) {
            this.orderAmount = orderAmount;
            return this;
        }

        public Builder userCount(Integer userCount) {
            this.userCount = userCount;
            return this;
        }

        public Builder productViewCount(Integer productViewCount) {
            this.productViewCount = productViewCount;
            return this;
        }

        public Builder orderConversionRate(BigDecimal orderConversionRate) {
            this.orderConversionRate = orderConversionRate;
            return this;
        }

        public Builder avgOrderAmount(BigDecimal avgOrderAmount) {
            this.avgOrderAmount = avgOrderAmount;
            return this;
        }

        public StatisticsReportResult build() {
            StatisticsReportResult result = new StatisticsReportResult();
            result.setDate(this.date);
            result.setOrderCount(this.orderCount);
            result.setOrderAmount(this.orderAmount);
            result.setUserCount(this.userCount);
            result.setProductViewCount(this.productViewCount);
            result.setOrderConversionRate(this.orderConversionRate);
            result.setAvgOrderAmount(this.avgOrderAmount);            return result;
        }
    }

    // 销售排行内部类
    public static class SalesRank {
        @ApiModelProperty(value = "商品ID")
        private Long productId;

        @ApiModelProperty(value = "商品名称")
        private String productName;

        @ApiModelProperty(value = "销售数量")
        private Integer saleCount;

        @ApiModelProperty(value = "销售金额")
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

        public Integer getSaleCount() {
            return saleCount;
        }

        public void setSaleCount(Integer saleCount) {
            this.saleCount = saleCount;
        }

        public BigDecimal getSaleAmount() {
            return saleAmount;
        }

        public void setSaleAmount(BigDecimal saleAmount) {
            this.saleAmount = saleAmount;
        }
    }

    // 每日趋势内部类
    public static class DailyTrend {
        @ApiModelProperty(value = "日期")
        private Date date;

        @ApiModelProperty(value = "订单数量")
        private Integer orderCount;

        @ApiModelProperty(value = "销售金额")
        private BigDecimal salesAmount;

        @ApiModelProperty(value = "新用户数量")
        private Integer newUserCount;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Integer getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }

        public BigDecimal getSalesAmount() {
            return salesAmount;
        }

        public void setSalesAmount(BigDecimal salesAmount) {
            this.salesAmount = salesAmount;
        }

        public Integer getNewUserCount() {
            return newUserCount;
        }

        public void setNewUserCount(Integer newUserCount) {
            this.newUserCount = newUserCount;
        }
    }

    // 每日统计内部类
    public static class DailyStatistics {
        @ApiModelProperty(value = "日期")
        private Date date;

        @ApiModelProperty(value = "订单数量")
        private Integer orderCount;

        @ApiModelProperty(value = "订单金额")
        private BigDecimal orderAmount;

        @ApiModelProperty(value = "用户数量")
        private Integer userCount;

        @ApiModelProperty(value = "商品访问量")
        private Integer productViewCount;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
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

        public Integer getProductViewCount() {
            return productViewCount;
        }

        public void setProductViewCount(Integer productViewCount) {
            this.productViewCount = productViewCount;
        }
    }

    // 月度统计内部类
    public static class MonthlyStatistics {
        @ApiModelProperty(value = "年月")
        private String yearMonth;

        @ApiModelProperty(value = "订单数量")
        private Integer orderCount;

        @ApiModelProperty(value = "订单金额")
        private BigDecimal orderAmount;

        @ApiModelProperty(value = "用户数量")
        private Integer userCount;

        @ApiModelProperty(value = "商品访问量")
        private Integer productViewCount;

        public String getYearMonth() {
            return yearMonth;
        }

        public void setYearMonth(String yearMonth) {
            this.yearMonth = yearMonth;
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

        public Integer getProductViewCount() {
            return productViewCount;
        }

        public void setProductViewCount(Integer productViewCount) {
            this.productViewCount = productViewCount;
        }
    }
}
