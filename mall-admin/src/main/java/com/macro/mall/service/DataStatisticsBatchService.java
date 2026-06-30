package com.macro.mall.service;

/**
 * 数据统计批处理服务接口
 * Created by macro on 2025/07/22.
 */
public interface DataStatisticsBatchService {
    
    /**
     * 计算每日销售额统计
     */
    void calculateDailySales();
    
    /**
     * 计算每日订单数量统计
     */
    void calculateDailyOrderCount();
    
    /**
     * 计算每日用户注册数量统计
     */
    void calculateDailyUserRegistrations();
    
    /**
     * 生成每日报表文件
     * @return 报表文件路径
     */
    String generateDailyReport();
    
    /**
     * 清理过期的统计数据
     * @return 清理的记录数量
     */
    int cleanupExpiredStatistics();
    
    /**
     * 生成月度归档文件
     * @return 归档文件路径
     */
    String generateMonthlyArchive();
}
