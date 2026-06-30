package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsProductParam;
import com.macro.mall.service.PmsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 日货商城测试Controller
 */
@Api(tags = "日货商城测试接口")
@RestController
@RequestMapping("/cigarette/test")
public class CigaretteTestController {
    
    @Autowired
    private PmsProductService productService;
    
    @ApiOperation("创建测试香烟产品")
    @PostMapping("/create")
    public CommonResult<String> createTestCigaretteProduct() {
        
        // 创建一个测试香烟产品
        PmsProductParam productParam = new PmsProductParam();
        productParam.setName("测试香烟产品");
        productParam.setSubTitle("用于测试香烟属性功能");
        productParam.setBrandId(1L);
        productParam.setPrice(new BigDecimal("100.00"));
        productParam.setOriginalPrice(new BigDecimal("100.00"));
        productParam.setStock(1000);
        productParam.setUnit("条");
        productParam.setWeight(new BigDecimal("20.0"));
        productParam.setSort(1);
        productParam.setDeleteStatus(0);
        productParam.setPublishStatus(1);
        productParam.setNewStatus(1);
        productParam.setRecommandStatus(1);
        productParam.setDetailTitle("测试香烟产品详情");
        productParam.setDetailDesc("这是一个测试香烟产品，用于验证香烟属性功能");
        productParam.setKeywords("测试,香烟,属性");
        productParam.setNote("测试产品");
        productParam.setAlbumPics("test.jpg");
        productParam.setDetailHtml("<p>测试香烟产品详情</p>");
        productParam.setDetailMobileHtml("<p>测试香烟产品详情</p>");
        productParam.setPromotionStartTime(new Date());
        productParam.setPromotionEndTime(new Date(System.currentTimeMillis() + 86400000)); // 24小时后
        
        try {
            int result = productService.create(productParam);
            if (result > 0) {
                return CommonResult.success("测试香烟产品创建成功，产品ID：" + productParam.getId());
            } else {
                return CommonResult.failed("测试香烟产品创建失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("创建失败：" + e.getMessage());
        }
    }
}
