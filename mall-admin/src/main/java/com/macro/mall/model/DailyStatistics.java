package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 每日数据统计实体类
 * Created by macro on 2025/07/22.
 */
public class DailyStatistics implements Serializable {
    
    @ApiModelProperty(value = "统计记录ID")
    private Long id;
    
    @ApiModelProperty(value = "统计日期")
    private Date statisticsDate;
    
    @ApiModelProperty(value = "当日销售额")
    private BigDecimal dailySales;
    
    @ApiModelProperty(value = "当日订单数量")
    private Long dailyOrderCount;
    
    @ApiModelProperty(value = "当日新增用户数")
    private Long dailyNewUsers;
    
    @ApiModelProperty(value = "当日已完成订单数")
    private Long dailyCompletedOrders;
    
    @ApiModelProperty(value = "当日已取消订单数")
    private Long dailyCancelledOrders;
    
    @ApiModelProperty(value = "当日平均订单金额")
    private BigDecimal dailyAvgOrderAmount;
    
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    
    private static final long serialVersionUID = 1L;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getStatisticsDate() {
        return statisticsDate;
    }
    
    public void setStatisticsDate(Date statisticsDate) {
        this.statisticsDate = statisticsDate;
    }
    
    public BigDecimal getDailySales() {
        return dailySales;
    }
    
    public void setDailySales(BigDecimal dailySales) {
        this.dailySales = dailySales;
    }
    
    public Long getDailyOrderCount() {
        return dailyOrderCount;
    }
    
    public void setDailyOrderCount(Long dailyOrderCount) {
        this.dailyOrderCount = dailyOrderCount;
    }
    
    public Long getDailyNewUsers() {
        return dailyNewUsers;
    }
    
    public void setDailyNewUsers(Long dailyNewUsers) {
        this.dailyNewUsers = dailyNewUsers;
    }
    
    public Long getDailyCompletedOrders() {
        return dailyCompletedOrders;
    }
    
    public void setDailyCompletedOrders(Long dailyCompletedOrders) {
        this.dailyCompletedOrders = dailyCompletedOrders;
    }
    
    public Long getDailyCancelledOrders() {
        return dailyCancelledOrders;
    }
    
    public void setDailyCancelledOrders(Long dailyCancelledOrders) {
        this.dailyCancelledOrders = dailyCancelledOrders;
    }
    
    public BigDecimal getDailyAvgOrderAmount() {
        return dailyAvgOrderAmount;
    }
    
    public void setDailyAvgOrderAmount(BigDecimal dailyAvgOrderAmount) {
        this.dailyAvgOrderAmount = dailyAvgOrderAmount;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    @Override
    public String toString() {
        return "DailyStatistics{" +
                "id=" + id +
                ", statisticsDate=" + statisticsDate +
                ", dailySales=" + dailySales +
                ", dailyOrderCount=" + dailyOrderCount +
                ", dailyNewUsers=" + dailyNewUsers +
                ", dailyCompletedOrders=" + dailyCompletedOrders +
                ", dailyCancelledOrders=" + dailyCancelledOrders +
                ", dailyAvgOrderAmount=" + dailyAvgOrderAmount +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
