package com.macro.mall.service.impl;

import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderExample;
import com.macro.mall.model.OmsOrderOperateHistory;
import com.macro.mall.service.EmailNotificationService;
import com.macro.mall.service.OrderBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 订单批处理服务实现类
 * Created by macro on 2025/07/22.
 */
@Service
public class OrderBatchServiceImpl implements OrderBatchService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderBatchServiceImpl.class);
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    
    @Autowired
    @Qualifier("adminEmailNotificationService")
    private EmailNotificationService emailNotificationService;
    
    @Value("${order.timeout.minutes:1440}")
    private int orderTimeoutMinutes;
    
    @Override
    @Transactional
    public int cancelTimeoutPendingOrders() {
        logger.info("开始取消超时未支付订单，超时时间: {} 分钟", orderTimeoutMinutes);
        
        // 计算超时时间点
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -orderTimeoutMinutes);
        Date timeoutTime = calendar.getTime();
        
        // 查询超时的待付款订单
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria()
                .andStatusEqualTo(0) // 待付款状态
                .andDeleteStatusEqualTo(0) // 未删除
                .andCreateTimeLessThan(timeoutTime); // 创建时间早于超时时间点
        
        List<OmsOrder> timeoutOrders = orderMapper.selectByExample(example);
        
        if (CollectionUtils.isEmpty(timeoutOrders)) {
            logger.info("没有发现超时未支付订单");
            return 0;
        }
        
        int cancelledCount = 0;
        for (OmsOrder order : timeoutOrders) {
            try {
                // 更新订单状态为已关闭
                OmsOrder updateOrder = new OmsOrder();
                updateOrder.setId(order.getId());
                updateOrder.setStatus(4); // 4=已关闭
                updateOrder.setModifyTime(new Date());
                
                int result = orderMapper.updateByPrimaryKeySelective(updateOrder);
                
                if (result > 0) {
                    // 添加操作记录
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(order.getId());
                    history.setCreateTime(new Date());
                    history.setOperateMan("系统自动");
                    history.setOrderStatus(4);
                    history.setNote("订单超时未支付，系统自动取消");
                    orderOperateHistoryMapper.insert(history);
                    
                    // 发送取消通知邮件
                    // try {
                    //     emailNotificationService.sendOrderCancellationEmail(
                    //         order.getId(), 
                    //         order.getMemberId(), 
                    //         "订单超时未支付，已自动取消"
                    //     );
                    // } catch (Exception e) {
                    //     logger.warn("发送订单取消通知邮件失败，订单ID: {}", order.getId(), e);
                    // }
                    
                    cancelledCount++;
                    logger.debug("成功取消超时订单: {}", order.getOrderSn());
                }
            } catch (Exception e) {
                logger.error("取消超时订单失败，订单ID: {}", order.getId(), e);
            }
        }
        
        logger.info("成功取消 {} 个超时未支付订单", cancelledCount);
        return cancelledCount;
    }
    
    @Override
    public int retryFailedPaymentCallbacks() {
        logger.info("开始重试支付失败的回调");
        
        // 这里可以实现重试支付失败回调的逻辑
        // 例如：查询支付失败的记录，重新调用支付接口确认支付状态等
        
        // 由于这是示例实现，这里返回0
        // 在实际项目中，可以根据具体的支付系统来实现
        
        logger.info("支付回调重试处理完成");
        return 0;
    }
    
    @Override
    public int sendOrderStatusNotifications() {
        logger.info("开始发送订单状态变更通知");
        
        // 查询今天有状态变更的订单操作记录
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date todayStart = calendar.getTime();
        
        // 这里可以实现查询需要发送通知的订单状态变更记录
        // 并发送相应的通知邮件或短信
        
        // 由于这是示例实现，这里返回0
        // 在实际项目中，可以根据具体需求实现通知逻辑
        
        logger.info("订单状态变更通知发送完成");
        return 0;
    }
}
