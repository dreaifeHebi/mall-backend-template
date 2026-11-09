package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.model.PmsBrand;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.portal.domain.PmsPortalBrandInfo;

import java.util.List;

/**
 * 前台品牌管理Service
 * Created by macro on 2020/5/15.
 */
public interface PmsPortalBrandService {
    /**
     * 分页获取推荐品牌
     */
    List<PmsBrand> recommendList(Integer pageNum, Integer pageSize);

    /**
     * 获取品牌详情
     */
    PmsBrand detail(Long brandId);

    /**
     * 分页获取品牌关联商品
     */
    CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize);

    /**
     * 获取所有品牌信息列表（包含商品数量）
     */
    List<PmsPortalBrandInfo> getBrandInfoList();

    /**
     * 根据分类ID获取品牌信息列表（包含该分类下的商品数量）
     */
    List<PmsPortalBrandInfo> getBrandInfoListByCategory(Long categoryId);
}
