package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.portal.domain.PmsPortalProductDetail;
import com.macro.mall.portal.domain.PmsProductCategoryNode;
import com.macro.mall.portal.domain.PmsPortalCategoryInfo;
import com.macro.mall.portal.service.PmsPortalProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台商品管理Controller
 * Created by macro on 2020/4/6.
 */
@Controller
@Api(tags = "PmsPortalProductController")
@Tag(name = "PmsPortalProductController", description = "前台商品管理")
@RequestMapping("/product")
public class PmsPortalProductController {

    @Autowired
    private PmsPortalProductService portalProductService;

    @ApiOperation(value = "综合搜索、筛选、排序")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProduct>> search(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Long brandId,
                                                       @RequestParam(required = false) Long productCategoryId,
                                                       @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                       @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                                       @RequestParam(required = false) String sort,
                                                       @RequestParam(required = false) String order) {
        List<PmsProduct> productList = portalProductService.search(keyword, brandId, productCategoryId, pageNum, pageSize, sort, order);
        return CommonResult.success(CommonPage.restPage(productList));
    }

    @ApiOperation("获取销量排行商品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProduct>> getProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                               @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                               @RequestParam(value = "sort", defaultValue = "sale") String sort,
                                                               @RequestParam(value = "order", defaultValue = "desc") String order,
                                                               @RequestParam(value = "productCategoryId", required = false) Long productCategoryId,
                                                               @RequestParam(value = "brandId", required = false) Long brandId) {
        List<PmsProduct> productList = portalProductService.getProductList(pageNum, pageSize, sort, order, productCategoryId, brandId);
        return CommonResult.success(CommonPage.restPage(productList));
    }

    @ApiOperation("以树形结构获取所有商品分类")
    @RequestMapping(value = "/categoryTreeList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductCategoryNode>> categoryTreeList() {
        List<PmsProductCategoryNode> list = portalProductService.categoryTreeList();
        return CommonResult.success(list);
    }

    @ApiOperation("获取前台商品详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsPortalProductDetail> detail(@PathVariable Long id) {
        PmsPortalProductDetail productDetail = portalProductService.detail(id);
        return CommonResult.success(productDetail);
    }

    @ApiOperation("获取分类信息列表（包含上架商品数量）")
    @RequestMapping(value = "/categoryInfoList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsPortalCategoryInfo>> getCategoryInfoList() {
        List<PmsPortalCategoryInfo> categoryInfoList = portalProductService.getCategoryInfoList();
        return CommonResult.success(categoryInfoList);
    }

    @ApiOperation("根据分类ID获取分类信息（包含上架商品数量）")
    @RequestMapping(value = "/categoryInfo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsPortalCategoryInfo> getCategoryInfo(@PathVariable Long id) {
        PmsPortalCategoryInfo categoryInfo = portalProductService.getCategoryInfo(id);
        if (categoryInfo == null) {
            return CommonResult.failed("分类不存在");
        }
        return CommonResult.success(categoryInfo);
    }
}
