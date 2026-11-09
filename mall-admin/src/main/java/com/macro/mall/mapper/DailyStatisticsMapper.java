package com.macro.mall.mapper;

import com.macro.mall.model.DailyStatistics;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 每日数据统计Mapper接口
 * Created by macro on 2025/07/22.
 */
public interface DailyStatisticsMapper {
    
    /**
     * 插入统计记录
     */
    int insert(DailyStatistics record);
    
    /**
     * 根据统计日期查询记录
     */
    DailyStatistics selectByStatisticsDate(@Param("statisticsDate") Date statisticsDate);
    
    /**
     * 更新统计记录
     */
    int updateByPrimaryKeySelective(DailyStatistics record);
    
    /**
     * 根据日期范围查询统计记录
     */
    List<DailyStatistics> selectByDateRange(@Param("startDate") Date startDate, 
                                           @Param("endDate") Date endDate);
    
    /**
     * 删除指定日期之前的记录
     */
    int deleteByDateBefore(@Param("beforeDate") Date beforeDate);
}
