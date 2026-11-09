package com.macro.mall.service;

import com.macro.mall.dto.OrderStatisticsDto;

import java.util.List;

/**
 * 统计服务
 * Created by mall on 2025/06/22.
 */
public interface StatisticsService {
    /**
     * 获取订单统计数据
     * @param startDate 开始日期 (YYYY-MM-DD)
     * @param endDate 结束日期 (YYYY-MM-DD)
     * @return 订单统计数据列表
     */
    List<OrderStatisticsDto> getOrderStatistics(String startDate, String endDate);
}
