package com.macro.mall.task;

import com.macro.mall.service.refund.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 退款状态查询定时任务
 * @author dreaifekks
 * @date 2025/10/13
 */
@Component
public class RefundStatusQueryTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefundStatusQueryTask.class);

    @Autowired
    private RefundService refundService;

    /**
     * 每30分钟执行一次退款状态查询
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30分钟
    public void queryPendingRefundStatus() {
        try {
            LOGGER.info("开始执行定时退款状态查询任务");
            refundService.autoQueryPendingRefunds();
            LOGGER.info("定时退款状态查询任务执行完成");
        } catch (Exception e) {
            LOGGER.error("定时退款状态查询任务执行异常", e);
        }
    }
}
