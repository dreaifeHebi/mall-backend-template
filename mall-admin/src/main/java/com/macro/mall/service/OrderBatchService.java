package com.macro.mall.service;

/**
 * 订单批处理服务接口
 * Created by macro on 2025/07/22.
 */
public interface OrderBatchService {
    
    /**
     * 自动取消超时未支付订单
     * @return 取消的订单数量
     */
    int cancelTimeoutPendingOrders();
    
    /**
     * 重试支付失败的回调
     * @return 重试的回调数量
     */
    int retryFailedPaymentCallbacks();
    
    /**
     * 发送订单状态变更通知
     * @return 发送的通知数量
     */
    int sendOrderStatusNotifications();
}
