package com.macro.mall.dao;

import com.macro.mall.dto.OrderStatisticsDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 统计查询DAO
 * Created by mall on 2025/06/22.
 */
public interface StatisticsDao {
    /**
     * 获取订单统计数据
     */
    List<OrderStatisticsDto> getOrderStatistics(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
