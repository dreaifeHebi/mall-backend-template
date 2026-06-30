package com.macro.mall.service;

import com.macro.mall.dto.OmsOrderCsvExportParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 订单CSV导出服务
 * Created on 2025/7/3.
 */
public interface OmsOrderCsvExportService {
    
    /**
     * 导出订单CSV（自定义时间范围）
     * @param exportParam 导出参数
     * @param response HTTP响应
     */
    void exportOrdersCsv(OmsOrderCsvExportParam exportParam, HttpServletResponse response);
    
    /**
     * 导出当前月份订单CSV
     * @param response HTTP响应
     */
    void exportCurrentMonthOrdersCsv(HttpServletResponse response);
    
    /**
     * 导出指定订单ID列表的CSV
     * @param orderIds 订单ID列表
     * @param response HTTP响应
     */
    void exportOrdersCsvByIds(List<Long> orderIds, HttpServletResponse response);
}
