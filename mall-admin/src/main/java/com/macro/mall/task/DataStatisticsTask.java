package com.macro.mall.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 数据统计定时任务
 * 每日定时统计各项业务数据，生成报表数据
 */
@Component
public class DataStatisticsTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStatisticsTask.class);
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * 每日凌晨2点执行数据统计
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void generateDailyStatistics() {
        try {
            LOGGER.info("开始执行每日数据统计任务");
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String dateStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // 统计订单数据
            statisticsOrderData(dateStr);
            
            // 统计用户数据
            statisticsUserData(dateStr);
            
            // 统计商品数据
            statisticsProductData(dateStr);
            
            LOGGER.info("每日数据统计任务执行完成");
        } catch (Exception e) {
            LOGGER.error("每日数据统计任务执行失败", e);
        }
    }
    
    /**
     * 统计订单数据
     */
    @Transactional
    private void statisticsOrderData(String dateStr) {        String insertSql = "INSERT INTO daily_statistics (stat_date, stat_type, stat_key, stat_value, create_time) " +
            "SELECT ?, 'order', stat_key, stat_value, NOW() FROM ( " +
            "SELECT 'total_orders' as stat_key, COUNT(*) as stat_value FROM oms_order WHERE DATE(create_time) = ? " +
            "UNION ALL " +
            "SELECT 'total_amount' as stat_key, COALESCE(SUM(total_amount), 0) as stat_value FROM oms_order WHERE DATE(create_time) = ? AND status IN (2, 3, 4) " +
            "UNION ALL " +
            "SELECT 'paid_orders' as stat_key, COUNT(*) as stat_value FROM oms_order WHERE DATE(create_time) = ? AND status IN (2, 3, 4) " +
            "UNION ALL " +
            "SELECT 'cancelled_orders' as stat_key, COUNT(*) as stat_value FROM oms_order WHERE DATE(create_time) = ? AND status = 4 " +
            ") t";
            
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            // 删除当日已有统计数据
            deleteExistingStats(conn, dateStr, "order");
            
            pstmt.setString(1, dateStr);
            pstmt.setString(2, dateStr);
            pstmt.setString(3, dateStr);
            pstmt.setString(4, dateStr);
            pstmt.setString(5, dateStr);
            
            int rows = pstmt.executeUpdate();
            LOGGER.info("订单统计数据插入完成，影响行数: {}", rows);
            
        } catch (SQLException e) {
            LOGGER.error("订单数据统计失败", e);
            throw new RuntimeException("订单数据统计失败", e);
        }
    }
    
    /**
     * 统计用户数据
     */
    @Transactional
    private void statisticsUserData(String dateStr) {        String insertSql = "INSERT INTO daily_statistics (stat_date, stat_type, stat_key, stat_value, create_time) " +
            "SELECT ?, 'user', stat_key, stat_value, NOW() FROM ( " +
            "SELECT 'new_members' as stat_key, COUNT(*) as stat_value FROM ums_member WHERE DATE(create_time) = ? " +
            "UNION ALL " +
            "SELECT 'active_members' as stat_key, COUNT(DISTINCT member_id) as stat_value FROM oms_order WHERE DATE(create_time) = ? " +
            "UNION ALL " +
            "SELECT 'total_members' as stat_key, COUNT(*) as stat_value FROM ums_member WHERE create_time <= ? + INTERVAL 1 DAY " +
            ") t";
            
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            // 删除当日已有统计数据
            deleteExistingStats(conn, dateStr, "user");
            
            pstmt.setString(1, dateStr);
            pstmt.setString(2, dateStr);
            pstmt.setString(3, dateStr);
            pstmt.setString(4, dateStr);
            
            int rows = pstmt.executeUpdate();
            LOGGER.info("用户统计数据插入完成，影响行数: {}", rows);
            
        } catch (SQLException e) {
            LOGGER.error("用户数据统计失败", e);
            throw new RuntimeException("用户数据统计失败", e);
        }
    }
    
    /**
     * 统计商品数据
     */
    @Transactional
    private void statisticsProductData(String dateStr) {        String insertSql = "INSERT INTO daily_statistics (stat_date, stat_type, stat_key, stat_value, create_time) " +
            "SELECT ?, 'product', stat_key, stat_value, NOW() FROM ( " +
            "SELECT 'new_products' as stat_key, COUNT(*) as stat_value FROM pms_product WHERE DATE(create_time) = ? " +
            "UNION ALL " +
            "SELECT 'total_products' as stat_key, COUNT(*) as stat_value FROM pms_product WHERE create_time <= ? + INTERVAL 1 DAY " +
            "UNION ALL " +
            "SELECT 'published_products' as stat_key, COUNT(*) as stat_value FROM pms_product WHERE create_time <= ? + INTERVAL 1 DAY AND publish_status = 1 " +
            ") t";
            
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            // 删除当日已有统计数据
            deleteExistingStats(conn, dateStr, "product");
            
            pstmt.setString(1, dateStr);
            pstmt.setString(2, dateStr);
            pstmt.setString(3, dateStr);
            pstmt.setString(4, dateStr);
            
            int rows = pstmt.executeUpdate();
            LOGGER.info("商品统计数据插入完成，影响行数: {}", rows);
            
        } catch (SQLException e) {
            LOGGER.error("商品数据统计失败", e);
            throw new RuntimeException("商品数据统计失败", e);
        }
    }
    
    /**
     * 删除已有统计数据
     */
    private void deleteExistingStats(Connection conn, String dateStr, String statType) throws SQLException {
        String deleteSql = "DELETE FROM daily_statistics WHERE stat_date = ? AND stat_type = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, dateStr);
            pstmt.setString(2, statType);
            int deleted = pstmt.executeUpdate();
            if (deleted > 0) {
                LOGGER.info("删除已有{}统计数据，删除行数: {}", statType, deleted);
            }
        }
    }
    
    /**
     * 手动触发统计（用于测试或补数据）
     */
    public void manualStatistics(String dateStr) {
        try {
            LOGGER.info("开始执行手动数据统计任务，日期: {}", dateStr);
            
            statisticsOrderData(dateStr);
            statisticsUserData(dateStr);
            statisticsProductData(dateStr);
            
            LOGGER.info("手动数据统计任务执行完成，日期: {}", dateStr);
        } catch (Exception e) {
            LOGGER.error("手动数据统计任务执行失败", e);
            throw new RuntimeException("手动数据统计任务执行失败", e);
        }
    }
}
