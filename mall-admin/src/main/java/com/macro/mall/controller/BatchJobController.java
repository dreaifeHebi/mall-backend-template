package com.macro.mall.controller;

import com.macro.mall.batch.DataStatisticsBatch;
import com.macro.mall.batch.OrderProcessingBatch;
import com.macro.mall.common.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 定时任务管理Controller
 * 提供手动触发定时任务的功能
 * Created by macro on 2025/07/22.
 */
@Controller
@Api(tags = "BatchJobController")
@Tag(name = "BatchJobController", description = "定时任务管理")
@RequestMapping("/batch")
public class BatchJobController {
    
    @Autowired
    private OrderProcessingBatch orderProcessingBatch;
    
    @Autowired
    private DataStatisticsBatch dataStatisticsBatch;
    
    @ApiOperation("手动触发订单处理任务")
    @RequestMapping(value = "/triggerOrderProcessing", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> triggerOrderProcessing() {
        try {
            orderProcessingBatch.processOrders();
            return CommonResult.success(null, "订单处理任务执行成功");
        } catch (Exception e) {
            return CommonResult.failed("订单处理任务执行失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("手动触发数据统计任务")
    @RequestMapping(value = "/triggerDataStatistics", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> triggerDataStatistics() {
        try {
            dataStatisticsBatch.generateDailyStatistics();
            return CommonResult.success(null, "数据统计任务执行成功");
        } catch (Exception e) {
            return CommonResult.failed("数据统计任务执行失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("手动触发月度归档任务")
    @RequestMapping(value = "/triggerMonthlyArchive", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> triggerMonthlyArchive() {
        try {
            dataStatisticsBatch.generateMonthlyArchive();
            return CommonResult.success(null, "月度归档任务执行成功");
        } catch (Exception e) {
            return CommonResult.failed("月度归档任务执行失败: " + e.getMessage());
        }
    }
}
