package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsProductCategoryParam;
import com.macro.mall.dto.PmsProductCategoryWithChildrenItem;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.service.PmsProductCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类管理Controller
 * Created by macro on 2018/4/26.
 */
@Controller
@Api(tags = "PmsProductCategoryController")
@Tag(name = "PmsProductCategoryController", description = "商品分类管理")
@RequestMapping("/productCategory")
public class PmsProductCategoryController {
    @Autowired
    private PmsProductCategoryService productCategoryService;

    @ApiOperation("添加商品分类")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@Validated @RequestBody PmsProductCategoryParam productCategoryParam) {
        int count = productCategoryService.create(productCategoryParam);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("修改商品分类")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id,
                         @Validated
                         @RequestBody PmsProductCategoryParam productCategoryParam) {
        int count = productCategoryService.update(id, productCategoryParam);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("分页查询商品分类")
    @RequestMapping(value = "/list/{parentId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProductCategory>> getList(@PathVariable Long parentId,
                                                                @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsProductCategory> productCategoryList = productCategoryService.getList(parentId, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(productCategoryList));
    }

    @ApiOperation("根据id获取商品分类")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsProductCategory> getItem(@PathVariable Long id) {
        PmsProductCategory productCategory = productCategoryService.getItem(id);
        return CommonResult.success(productCategory);
    }

    @ApiOperation("删除商品分类")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = productCategoryService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("修改导航栏显示状态")
    @RequestMapping(value = "/update/navStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateNavStatus(@RequestParam("ids") List<Long> ids, @RequestParam("navStatus") Integer navStatus) {
        int count = productCategoryService.updateNavStatus(ids, navStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("修改显示状态")
    @RequestMapping(value = "/update/showStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateShowStatus(@RequestParam("ids") List<Long> ids, @RequestParam("showStatus") Integer showStatus) {
        int count = productCategoryService.updateShowStatus(ids, showStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("查询所有一级分类及子分类")
    @RequestMapping(value = "/list/withChildren", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductCategoryWithChildrenItem>> listWithChildren() {
        List<PmsProductCategoryWithChildrenItem> list = productCategoryService.listWithChildren();
        return CommonResult.success(list);
    }

    @ApiOperation("前台更新分类信息（接收图片URL）")
    @RequestMapping(value = "/frontend/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> updateCategoryForFrontend(
            @PathVariable Long id,
            @ApiParam("分类名称") @RequestParam(required = false) String name,
            @ApiParam("分类子标题") @RequestParam(required = false) String subTitle,
            @ApiParam("分类图片URL") @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @ApiParam("分类排序") @RequestParam(value = "sort", required = false) Integer sort) {
        
        System.out.println("=== Controller收到参数 ===");
        System.out.println("ID: " + id);
        System.out.println("name: " + name);
        System.out.println("subTitle: " + subTitle);
        System.out.println("imageUrl: " + imageUrl);
        System.out.println("sort: " + sort);
        System.out.println("========================");
        
        try {
            int count = productCategoryService.updateCategoryForFrontend(id, name, subTitle, imageUrl, sort);
            if (count > 0) {
                return CommonResult.success("更新成功");
            }
            return CommonResult.failed("更新失败");
            
        } catch (Exception e) {
            return CommonResult.failed("更新失败: " + e.getMessage());
        }
    }

    @ApiOperation("前台新增分类（接收图片URL）")
    @RequestMapping(value = "/frontend/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Long> createCategoryForFrontend(
            @ApiParam("分类名称") @RequestParam String name,
            @ApiParam("分类子标题") @RequestParam(required = false) String subTitle,
            @ApiParam("分类图片URL") @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @ApiParam("父分类ID，0表示顶级分类") @RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId,
            @ApiParam("分类排序") @RequestParam(value = "sort", required = false) Integer sort) {
        
        try {
            Long categoryId = productCategoryService.createCategoryForFrontend(name, subTitle, imageUrl, parentId, sort);
            return CommonResult.success(categoryId);
            
        } catch (IllegalArgumentException e) {
            return CommonResult.failed("参数错误: " + e.getMessage());
        } catch (Exception e) {
            return CommonResult.failed("创建失败: " + e.getMessage());
        }
    }
}
