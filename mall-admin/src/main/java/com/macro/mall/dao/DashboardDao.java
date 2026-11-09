package com.macro.mall.dao;

/**
 * 仪表盘查询DAO
 * Created by mall on 2025/06/22.
 */
public interface DashboardDao {
    /**
     * 获取今日订单数
     */
    Integer getTodayOrderCount();

    /**
     * 获取今日订单金额(分)
     */
    Long getTodayOrderAmount();

    /**
     * 获取总用户数
     */
    Integer getTotalUserCount();

    /**
     * 获取总商品数
     */
    Integer getTotalProductCount();

    /**
     * 获取本月订单数
     */
    Integer getMonthOrderCount();

    /**
     * 获取本月订单金额(分)
     */
    Long getMonthOrderAmount();

    /**
     * 获取待付款订单数
     */
    Integer getPendingPaymentCount();

    /**
     * 获取已完成订单数
     */
    Integer getCompletedOrderCount();

    /**
     * 获取待收货订单数
     */
    Integer getPendingReceiveCount();

    /**
     * 获取待发货订单数
     */
    Integer getPendingShipmentCount();

    /**
     * 获取缺货商品数
     */
    Integer getStockOutCount();

    /**
     * 获取待退款订单数
     */
    Integer getPendingRefundCount();

    /**
     * 获取已发货订单数
     */
    Integer getShippedOrderCount();

    /**
     * 获取待退货订单数
     */
    Integer getPendingReturnCount();

    /**
     * 获取下架商品数
     */
    Integer getProductOfflineCount();

    /**
     * 获取上架商品数
     */
    Integer getProductOnlineCount();

    /**
     * 获取库存不足商品数
     */
    Integer getProductLowStockCount();

    /**
     * 获取今日新增用户数
     */
    Integer getTodayNewUserCount();

    /**
     * 获取昨日新增用户数
     */
    Integer getYesterdayNewUserCount();

    /**
     * 获取本月新增用户数
     */
    Integer getMonthNewUserCount();

    /**
     * 获取本周订单数
     */
    Integer getWeekOrderCount();

    /**
     * 获取本周订单金额(分)
     */
    Long getWeekOrderAmount();

    /**
     * 获取上月订单数
     */
    Integer getLastMonthOrderCount();

    /**
     * 获取上月订单金额(分)
     */
    Long getLastMonthOrderAmount();

    /**
     * 获取上周订单数
     */
    Integer getLastWeekOrderCount();

    /**
     * 获取上周订单金额(分)
     */
    Long getLastWeekOrderAmount();
}
