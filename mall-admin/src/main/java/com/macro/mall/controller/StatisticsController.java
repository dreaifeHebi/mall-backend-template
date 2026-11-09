package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.OrderStatisticsDto;
import com.macro.mall.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计控制器
 * Created by mall on 2025/06/22.
 */
@Controller
@Api(tags = "StatisticsController")
@Tag(name = "StatisticsController", description = "统计管理")
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("获取订单统计数据")
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OrderStatisticsDto>> getOrderStatistics(
            @ApiParam(value = "开始日期", required = true) @RequestParam String startDate,
            @ApiParam(value = "结束日期", required = true) @RequestParam String endDate) {
        List<OrderStatisticsDto> result = statisticsService.getOrderStatistics(startDate, endDate);
        return CommonResult.success(result);
    }
}
