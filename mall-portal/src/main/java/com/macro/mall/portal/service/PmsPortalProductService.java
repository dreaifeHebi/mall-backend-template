package com.macro.mall.portal.service;

import com.macro.mall.model.PmsProduct;
import com.macro.mall.portal.domain.PmsPortalProductDetail;
import com.macro.mall.portal.domain.PmsProductCategoryNode;
import com.macro.mall.portal.domain.PmsPortalCategoryInfo;

import java.util.List;

/**
 * 前台商品管理Service
 * Created by macro on 2020/4/6.
 */
public interface PmsPortalProductService {
    /**
     * 综合搜索商品
     */
    List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, String sort, String order);

    /**
     * 获取商品列表（支持排序、分类筛选和品牌筛选）
     */
    List<PmsProduct> getProductList(Integer pageNum, Integer pageSize, String sort, String order, Long productCategoryId, Long brandId);

    /**
     * 以树形结构获取所有商品分类
     */
    List<PmsProductCategoryNode> categoryTreeList();

    /**
     * 获取前台商品详情
     */
    PmsPortalProductDetail detail(Long id);

    /**
     * 获取分类信息列表（包含上架商品数量）
     */
    List<PmsPortalCategoryInfo> getCategoryInfoList();

    /**
     * 根据分类ID获取分类信息（包含上架商品数量）
     */
    PmsPortalCategoryInfo getCategoryInfo(Long categoryId);
}
