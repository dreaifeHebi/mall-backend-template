package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.mapper.PmsBrandMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsBrand;
import com.macro.mall.model.PmsBrandExample;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductExample;
import com.macro.mall.portal.dao.HomeDao;
import com.macro.mall.portal.domain.PmsPortalBrandInfo;
import com.macro.mall.portal.service.PmsPortalBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 前台品牌管理Service实现类
 * Created by macro on 2020/5/15.
 */
@Service
public class PmsPortalBrandServiceImpl implements PmsPortalBrandService {
    @Autowired
    private HomeDao homeDao;
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> recommendList(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return homeDao.getRecommendBrandList(offset, pageSize);
    }

    @Override
    public PmsBrand detail(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    @Override
    public CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andDeleteStatusEqualTo(0)
                .andPublishStatusEqualTo(1)
                .andBrandIdEqualTo(brandId);
        List<PmsProduct> productList = productMapper.selectByExample(example);
        return CommonPage.restPage(productList);
    }

    @Override
    public List<PmsPortalBrandInfo> getBrandInfoList() {
        // 获取所有品牌
        PmsBrandExample brandExample = new PmsBrandExample();
        brandExample.createCriteria().andShowStatusEqualTo(1);
        List<PmsBrand> brandList = brandMapper.selectByExample(brandExample);
        
        List<PmsPortalBrandInfo> brandInfoList = new ArrayList<>();
        for (PmsBrand brand : brandList) {
            PmsPortalBrandInfo brandInfo = new PmsPortalBrandInfo();
            brandInfo.setId(brand.getId());
            brandInfo.setName(brand.getName());
            brandInfo.setSubTitle(brand.getSubTitle());
            brandInfo.setLogoUrl(brand.getLogo());
            
            // 统计该品牌下已上架的商品数量
            PmsProductExample productExample = new PmsProductExample();
            productExample.createCriteria()
                .andDeleteStatusEqualTo(0)
                .andPublishStatusEqualTo(1)
                .andBrandIdEqualTo(brand.getId());
            int productCount = (int) productMapper.countByExample(productExample);
            brandInfo.setProductCount(productCount);
            
            brandInfoList.add(brandInfo);
        }
        
        return brandInfoList;
    }

    @Override
    public List<PmsPortalBrandInfo> getBrandInfoListByCategory(Long categoryId) {
        // 先获取该分类下有商品的品牌ID列表
        PmsProductExample productExample = new PmsProductExample();
        productExample.createCriteria()
            .andDeleteStatusEqualTo(0)
            .andPublishStatusEqualTo(1)
            .andProductCategoryIdEqualTo(categoryId);
        List<PmsProduct> productList = productMapper.selectByExample(productExample);
        
        // 提取品牌ID并去重
        List<Long> brandIds = new ArrayList<>();
        for (PmsProduct product : productList) {
            if (product.getBrandId() != null && !brandIds.contains(product.getBrandId())) {
                brandIds.add(product.getBrandId());
            }
        }
        
        if (brandIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取品牌信息
        PmsBrandExample brandExample = new PmsBrandExample();
        brandExample.createCriteria()
            .andShowStatusEqualTo(1)
            .andIdIn(brandIds);
        List<PmsBrand> brandList = brandMapper.selectByExample(brandExample);
        
        List<PmsPortalBrandInfo> brandInfoList = new ArrayList<>();
        for (PmsBrand brand : brandList) {
            PmsPortalBrandInfo brandInfo = new PmsPortalBrandInfo();
            brandInfo.setId(brand.getId());
            brandInfo.setName(brand.getName());
            brandInfo.setSubTitle(brand.getSubTitle());
            brandInfo.setLogoUrl(brand.getLogo());
            
            // 统计该品牌在指定分类下的商品数量
            PmsProductExample categoryProductExample = new PmsProductExample();
            categoryProductExample.createCriteria()
                .andDeleteStatusEqualTo(0)
                .andPublishStatusEqualTo(1)
                .andBrandIdEqualTo(brand.getId())
                .andProductCategoryIdEqualTo(categoryId);
            int productCount = (int) productMapper.countByExample(categoryProductExample);
            brandInfo.setProductCount(productCount);
            
            brandInfoList.add(brandInfo);
        }
        
        return brandInfoList;
    }
}
