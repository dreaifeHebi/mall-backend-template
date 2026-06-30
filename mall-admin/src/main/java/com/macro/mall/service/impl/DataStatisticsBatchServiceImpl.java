package com.macro.mall.service.impl;

import com.macro.mall.mapper.DailyStatisticsMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.DailyStatistics;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderExample;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.service.DataStatisticsBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 数据统计批处理服务实现类
 * Created by macro on 2025/07/22.
 */
@Service
public class DataStatisticsBatchServiceImpl implements DataStatisticsBatchService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataStatisticsBatchServiceImpl.class);
    
    @Autowired
    private DailyStatisticsMapper dailyStatisticsMapper;
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Value("${statistics.report.path:/var/reports/daily}")
    private String reportBasePath;
    
    @Value("${statistics.archive.path:/var/reports/archive}")
    private String archiveBasePath;
    
    @Value("${statistics.retention.days:365}")
    private int retentionDays;
    
    @Override
    @Transactional
    public void calculateDailySales() {
        Date yesterday = getYesterday();
        logger.info("开始计算{}的每日销售额统计", formatDate(yesterday));
        
        // 查询前一天的所有已完成或已发货订单
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria()
                .andCreateTimeBetween(getStartOfDay(yesterday), getEndOfDay(yesterday))
                .andStatusIn(Arrays.asList(2, 3)) // 2=已发货, 3=已完成
                .andDeleteStatusEqualTo(0);
        
        List<OmsOrder> orders = orderMapper.selectByExample(example);
        
        BigDecimal totalSales = BigDecimal.ZERO;
        for (OmsOrder order : orders) {
            if (order.getPayAmount() != null) {
                totalSales = totalSales.add(order.getPayAmount());
            }
        }
        
        // 更新或插入统计记录
        DailyStatistics statistics = getOrCreateDailyStatistics(yesterday);
        statistics.setDailySales(totalSales);
        
        updateStatistics(statistics);
        
        logger.info("{}的销售额统计完成，总销售额: {}", formatDate(yesterday), totalSales);
    }
    
    @Override
    @Transactional
    public void calculateDailyOrderCount() {
        Date yesterday = getYesterday();
        logger.info("开始计算{}的每日订单量统计", formatDate(yesterday));
        
        // 查询前一天的所有订单
        OmsOrderExample totalExample = new OmsOrderExample();
        totalExample.createCriteria()
                .andCreateTimeBetween(getStartOfDay(yesterday), getEndOfDay(yesterday))
                .andDeleteStatusEqualTo(0);
        
        long totalOrders = orderMapper.countByExample(totalExample);
        
        // 查询已完成订单数
        OmsOrderExample completedExample = new OmsOrderExample();
        completedExample.createCriteria()
                .andCreateTimeBetween(getStartOfDay(yesterday), getEndOfDay(yesterday))
                .andStatusEqualTo(3) // 3=已完成
                .andDeleteStatusEqualTo(0);
        
        long completedOrders = orderMapper.countByExample(completedExample);
        
        // 查询已取消订单数
        OmsOrderExample cancelledExample = new OmsOrderExample();
        cancelledExample.createCriteria()
                .andCreateTimeBetween(getStartOfDay(yesterday), getEndOfDay(yesterday))
                .andStatusEqualTo(4) // 4=已关闭/取消
                .andDeleteStatusEqualTo(0);
        
        long cancelledOrders = orderMapper.countByExample(cancelledExample);
        
        // 计算平均订单金额
        BigDecimal avgOrderAmount = BigDecimal.ZERO;
        if (totalOrders > 0) {
            OmsOrderExample avgExample = new OmsOrderExample();
            avgExample.createCriteria()
                    .andCreateTimeBetween(getStartOfDay(yesterday), getEndOfDay(yesterday))
                    .andDeleteStatusEqualTo(0);
            
            List<OmsOrder> orders = orderMapper.selectByExample(avgExample);
            BigDecimal totalAmount = BigDecimal.ZERO;
            int validOrderCount = 0;
            
            for (OmsOrder order : orders) {
                if (order.getPayAmount() != null) {
                    totalAmount = totalAmount.add(order.getPayAmount());
                    validOrderCount++;
                }
            }
            
            if (validOrderCount > 0) {
                avgOrderAmount = totalAmount.divide(BigDecimal.valueOf(validOrderCount), 2, RoundingMode.HALF_UP);
            }
        }
        
        // 更新统计记录
        DailyStatistics statistics = getOrCreateDailyStatistics(yesterday);
        statistics.setDailyOrderCount(totalOrders);
        statistics.setDailyCompletedOrders(completedOrders);
        statistics.setDailyCancelledOrders(cancelledOrders);
        statistics.setDailyAvgOrderAmount(avgOrderAmount);
        
        updateStatistics(statistics);
        
        logger.info("{}的订单量统计完成，总订单数: {}, 已完成: {}, 已取消: {}, 平均金额: {}", 
                formatDate(yesterday), totalOrders, completedOrders, cancelledOrders, avgOrderAmount);
    }
    
    @Override
    @Transactional
    public void calculateDailyUserRegistrations() {
        Date yesterday = getYesterday();
        logger.info("开始计算{}的每日用户注册量统计", formatDate(yesterday));
        
        // 查询前一天的新注册用户
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria()
                .andCreateTimeBetween(getStartOfDay(yesterday), getEndOfDay(yesterday));
        
        long newUsers = memberMapper.countByExample(example);
        
        // 更新统计记录
        DailyStatistics statistics = getOrCreateDailyStatistics(yesterday);
        statistics.setDailyNewUsers(newUsers);
        
        updateStatistics(statistics);
        
        logger.info("{}的用户注册量统计完成，新注册用户数: {}", formatDate(yesterday), newUsers);
    }
    
    @Override
    public String generateDailyReport() {
        Date yesterday = getYesterday();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(yesterday);
        String fileName = String.format("daily_report_%s.csv", dateStr);
        String filePath = reportBasePath + "/" + fileName;
        
        logger.info("开始生成每日报表: {}", filePath);
        
        try {
            // 确保目录存在
            java.io.File directory = new java.io.File(reportBasePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            DailyStatistics statistics = dailyStatisticsMapper.selectByStatisticsDate(yesterday);
            
            try (FileWriter writer = new FileWriter(filePath)) {
                // 写入CSV头部
                writer.append("统计日期,销售额,订单数量,新增用户,已完成订单,已取消订单,平均订单金额\n");
                
                // 写入数据
                if (statistics != null) {
                    writer.append(String.format("%s,%s,%d,%d,%d,%d,%s\n",
                            dateStr,
                            statistics.getDailySales() != null ? statistics.getDailySales().toString() : "0",
                            statistics.getDailyOrderCount() != null ? statistics.getDailyOrderCount() : 0,
                            statistics.getDailyNewUsers() != null ? statistics.getDailyNewUsers() : 0,
                            statistics.getDailyCompletedOrders() != null ? statistics.getDailyCompletedOrders() : 0,
                            statistics.getDailyCancelledOrders() != null ? statistics.getDailyCancelledOrders() : 0,
                            statistics.getDailyAvgOrderAmount() != null ? statistics.getDailyAvgOrderAmount().toString() : "0"
                    ));
                } else {
                    writer.append(String.format("%s,0,0,0,0,0,0\n", dateStr));
                }
            }
            
            logger.info("每日报表生成完成: {}", filePath);
            return filePath;
            
        } catch (IOException e) {
            logger.error("生成每日报表失败", e);
            throw new RuntimeException("生成每日报表失败", e);
        }
    }
    
    @Override
    @Transactional
    public int cleanupExpiredStatistics() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -retentionDays);
        Date expiredDate = calendar.getTime();
        
        logger.info("开始清理{}之前的统计数据", formatDate(expiredDate));
        
        int deletedCount = dailyStatisticsMapper.deleteByDateBefore(expiredDate);
        
        logger.info("清理了 {} 条过期统计数据", deletedCount);
        return deletedCount;
    }
    
    @Override
    public String generateMonthlyArchive() {
        // 获取上个月的年月
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        String yearMonth = lastMonth.toString().substring(0, 7); // 格式: 2025-07
        String fileName = String.format("monthly_archive_%s.csv", yearMonth);
        String filePath = archiveBasePath + "/" + fileName;
        
        logger.info("开始生成月度归档: {}", filePath);
        
        try {
            // 确保目录存在
            java.io.File directory = new java.io.File(archiveBasePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // 获取上个月的开始和结束日期
            Date startDate = Date.from(lastMonth.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
            
            List<DailyStatistics> monthlyData = dailyStatisticsMapper.selectByDateRange(startDate, endDate);
            
            try (FileWriter writer = new FileWriter(filePath)) {
                // 写入CSV头部
                writer.append("统计日期,销售额,订单数量,新增用户,已完成订单,已取消订单,平均订单金额\n");
                
                // 写入数据
                for (DailyStatistics statistics : monthlyData) {
                    writer.append(String.format("%s,%s,%d,%d,%d,%d,%s\n",
                            new SimpleDateFormat("yyyy-MM-dd").format(statistics.getStatisticsDate()),
                            statistics.getDailySales() != null ? statistics.getDailySales().toString() : "0",
                            statistics.getDailyOrderCount() != null ? statistics.getDailyOrderCount() : 0,
                            statistics.getDailyNewUsers() != null ? statistics.getDailyNewUsers() : 0,
                            statistics.getDailyCompletedOrders() != null ? statistics.getDailyCompletedOrders() : 0,
                            statistics.getDailyCancelledOrders() != null ? statistics.getDailyCancelledOrders() : 0,
                            statistics.getDailyAvgOrderAmount() != null ? statistics.getDailyAvgOrderAmount().toString() : "0"
                    ));
                }
            }
            
            logger.info("月度归档生成完成: {}", filePath);
            return filePath;
            
        } catch (IOException e) {
            logger.error("生成月度归档失败", e);
            throw new RuntimeException("生成月度归档失败", e);
        }
    }
    
    // 辅助方法
    private Date getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }
    
    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    
    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    
    private DailyStatistics getOrCreateDailyStatistics(Date date) {
        DailyStatistics statistics = dailyStatisticsMapper.selectByStatisticsDate(date);
        if (statistics == null) {
            statistics = new DailyStatistics();
            statistics.setStatisticsDate(date);
            statistics.setCreateTime(new Date());
        }
        return statistics;
    }
    
    private void updateStatistics(DailyStatistics statistics) {
        statistics.setUpdateTime(new Date());
        
        if (statistics.getId() == null) {
            dailyStatisticsMapper.insert(statistics);
        } else {
            dailyStatisticsMapper.updateByPrimaryKeySelective(statistics);
        }
    }
}
