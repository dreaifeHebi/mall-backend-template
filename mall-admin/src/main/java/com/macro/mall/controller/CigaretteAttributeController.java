package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.impl.CigaretteAttributeServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 香烟属性管理Controller
 */
@Api(tags = "香烟属性管理")
@RestController
@RequestMapping("/cigarette")
public class CigaretteAttributeController {
    
    @Autowired
    private CigaretteAttributeServiceImpl cigaretteAttributeService;
    
    @ApiOperation("获取产品的香烟属性")
    @GetMapping("/attributes/{productId}")
    public CommonResult<Map<String, String>> getCigaretteAttributes(
            @ApiParam("产品ID") @PathVariable Long productId) {
        
        Map<String, String> attributes = cigaretteAttributeService.getCigaretteAttributes(productId);
        return CommonResult.success(attributes);
    }
    
    @ApiOperation("更新产品的香烟属性")
    @PutMapping("/attributes/{productId}")
    public CommonResult<String> updateCigaretteAttributes(
            @ApiParam("产品ID") @PathVariable Long productId,
            @ApiParam("香烟类型") @RequestParam(required = false) String cigaretteType,
            @ApiParam("焦油量(mg)") @RequestParam(required = false) String tarContent,
            @ApiParam("尼古丁含量(mg)") @RequestParam(required = false) String nicotineContent,
            @ApiParam("包装数量(支)") @RequestParam(required = false) String packageCount,
            @ApiParam("单支价格(元)") @RequestParam(required = false) String pricePerStick) {
        
        boolean success = cigaretteAttributeService.updateCigaretteAttributes(
            productId, cigaretteType, tarContent, nicotineContent, packageCount, pricePerStick);
        
        if (success) {
            return CommonResult.success("香烟属性更新成功");
        } else {
            return CommonResult.failed("香烟属性更新失败");
        }
    }
}
