package com.macro.mall.service.impl;

import com.macro.mall.dao.StatisticsDao;
import com.macro.mall.dto.OrderStatisticsDto;
import com.macro.mall.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 统计服务实现类
 * Created by mall on 2025/06/22.
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsDao statisticsDao;

    @Override
    public List<OrderStatisticsDto> getOrderStatistics(String startDate, String endDate) {
        return statisticsDao.getOrderStatistics(startDate, endDate);
    }
}
