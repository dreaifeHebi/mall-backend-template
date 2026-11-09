package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsBanner;
import com.macro.mall.portal.service.PortalBannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Portal端轮播图Controller
 * Created on 2025-10-12
 */
@Controller
@Api(tags = "PortalBannerController")
@Tag(name = "PortalBannerController", description = "轮播图展示")
@RequestMapping("/banner")
public class PortalBannerController {

    @Autowired
    private PortalBannerService bannerService;

    @ApiOperation("获取轮播图列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CmsBanner>> getBanners() {
        List<CmsBanner> banners = bannerService.getEnabledBanners();
        return CommonResult.success(banners);
    }
}