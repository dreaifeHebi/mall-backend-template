package com.macro.mall.batch;

import com.macro.mall.service.OrderBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单处理定时任务
 * 功能：处理状态为 PENDING 的订单，自动取消超时未支付订单；重试支付失败的回调，并发送通知
 * 执行频率：每 30 分钟执行一次
 * Created by macro on 2025/07/22.
 */
@Component
public class OrderProcessingBatch {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingBatch.class);
    
    @Autowired
    private OrderBatchService orderBatchService;
    
    /**
     * 订单处理定时任务
     * 每30分钟执行一次 (0 * /30 * * * ?)
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void processOrders() {
        logger.info("开始执行订单处理定时任务...");
        
        try {
            // 1. 自动取消超时未支付订单
            int cancelledOrders = orderBatchService.cancelTimeoutPendingOrders();
            logger.info("成功取消 {} 个超时未支付订单", cancelledOrders);
            
            // 2. 重试支付失败的回调
            int retryCount = orderBatchService.retryFailedPaymentCallbacks();
            logger.info("重试了 {} 个支付失败的回调", retryCount);
            
            // 3. 发送订单状态变更通知
            int notificationCount = orderBatchService.sendOrderStatusNotifications();
            logger.info("发送了 {} 个订单状态变更通知", notificationCount);
            
            logger.info("订单处理定时任务执行完成");
            
        } catch (Exception e) {
            logger.error("订单处理定时任务执行失败", e);
        }
    }
}
