package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.DashboardDto;
import com.macro.mall.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 首页仪表盘控制器
 * Created by mall on 2025/06/22.
 */
@Controller
@Api(tags = "HomeDashboardController")
@Tag(name = "HomeDashboardController", description = "首页仪表盘管理")
@RequestMapping("/admin")
public class HomeDashboardController {

    @Autowired
    private DashboardService dashboardService;

    @ApiOperation("获取首页仪表盘数据")
    @RequestMapping(value = "/homeDashboard", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<DashboardDto> getHomeDashboard() {
        DashboardDto result = dashboardService.getDashboardData();
        return CommonResult.success(result);
    }
}
