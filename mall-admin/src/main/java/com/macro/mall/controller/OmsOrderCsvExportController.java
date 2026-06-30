package com.macro.mall.controller;

import com.macro.mall.dto.OmsOrderCsvExportParam;
import com.macro.mall.service.OmsOrderCsvExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import com.macro.mall.dto.OmsOrderCsvExportParam;
import com.macro.mall.service.OmsOrderCsvExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

/**
 * 订单CSV导出Controller
 * Created on 2025/7/3.
 */
@Controller
@Api(tags = "OmsOrderCsvExportController")
@Tag(name = "OmsOrderCsvExportController", description = "订单CSV导出管理")
@RequestMapping("/order/csv")
public class OmsOrderCsvExportController {
    
    @Autowired
    private OmsOrderCsvExportService orderCsvExportService;
    
    @ApiOperation("导出订单CSV（自定义时间范围）")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportOrdersCsv(OmsOrderCsvExportParam exportParam, HttpServletResponse response) {
        orderCsvExportService.exportOrdersCsv(exportParam, response);
    }
    
    @ApiOperation("导出当前月份订单CSV")
    @RequestMapping(value = "/export/current-month", method = RequestMethod.GET)
    public void exportCurrentMonthOrdersCsv(HttpServletResponse response) {
        orderCsvExportService.exportCurrentMonthOrdersCsv(response);
    }
    
    @ApiOperation("导出指定订单ID列表的CSV")
    @RequestMapping(value = "/export/by-ids", method = RequestMethod.POST)
    public void exportOrdersCsvByIds(@RequestBody List<Long> orderIds, HttpServletResponse response) {
        orderCsvExportService.exportOrdersCsvByIds(orderIds, response);
    }
}
