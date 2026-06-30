package com.macro.mall.batch;

import com.macro.mall.service.DataStatisticsBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 数据统计定时任务
 * 功能：每日统计销售额、订单量、用户注册量等指标，生成报表并写入统计表
 * 执行频率：每天 00:10 自动触发
 * Created by macro on 2025/07/22.
 */
@Component
public class DataStatisticsBatch {
    
    private static final Logger logger = LoggerFactory.getLogger(DataStatisticsBatch.class);
    
    @Autowired
    private DataStatisticsBatchService dataStatisticsBatchService;
    
    /**
     * 数据统计定时任务
     * 每天凌晨00:10执行 (0 10 0 * * ?)
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void generateDailyStatistics() {
        logger.info("开始执行每日数据统计任务...");
        
        try {
            // 1. 统计前一天的销售额
            dataStatisticsBatchService.calculateDailySales();
            logger.info("完成每日销售额统计");
            
            // 2. 统计前一天的订单量
            dataStatisticsBatchService.calculateDailyOrderCount();
            logger.info("完成每日订单量统计");
            
            // 3. 统计前一天的用户注册量
            dataStatisticsBatchService.calculateDailyUserRegistrations();
            logger.info("完成每日用户注册量统计");
            
            // 4. 生成CSV报表文件
            String reportPath = dataStatisticsBatchService.generateDailyReport();
            logger.info("生成每日报表文件: {}", reportPath);
            
            // 5. 清理过期的统计数据（可选，根据配置决定保留时间）
            int cleanedRecords = dataStatisticsBatchService.cleanupExpiredStatistics();
            logger.info("清理了 {} 条过期统计数据", cleanedRecords);
            
            logger.info("每日数据统计任务执行完成");
            
        } catch (Exception e) {
            logger.error("每日数据统计任务执行失败", e);
        }
    }
    
    /**
     * 月度数据归档任务
     * 每月1日凌晨02:00执行 (0 0 2 1 * ?)
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void generateMonthlyArchive() {
        logger.info("开始执行月度数据归档任务...");
        
        try {
            // 生成上个月的月度报表并归档
            String archivePath = dataStatisticsBatchService.generateMonthlyArchive();
            logger.info("生成月度归档文件: {}", archivePath);
            
            logger.info("月度数据归档任务执行完成");
            
        } catch (Exception e) {
            logger.error("月度数据归档任务执行失败", e);
        }
    }
}
