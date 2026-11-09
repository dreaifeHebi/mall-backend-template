package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.PmsBrandParam;
import com.macro.mall.mapper.PmsBrandMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsBrand;
import com.macro.mall.model.PmsBrandExample;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductExample;
import com.macro.mall.service.PmsBrandService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品品牌管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class PmsBrandServiceImpl implements PmsBrandService {
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> listAllBrand() {
        return brandMapper.selectByExample(new PmsBrandExample());
    }

    @Override
    public int createBrand(PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        //如果创建时首字母为空，取名称的第一个为首字母
        if (StrUtil.isEmpty(pmsBrand.getFirstLetter())) {
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0, 1));
        }
        return brandMapper.insertSelective(pmsBrand);
    }

    @Override
    public int updateBrand(Long id, PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        pmsBrand.setId(id);
        //如果创建时首字母为空，取名称的第一个为首字母
        if (StrUtil.isEmpty(pmsBrand.getFirstLetter())) {
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0, 1));
        }
        //更新品牌时要更新商品中的品牌名称
        PmsProduct product = new PmsProduct();
        product.setBrandName(pmsBrand.getName());
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andBrandIdEqualTo(id);
        productMapper.updateByExampleSelective(product,example);
        return brandMapper.updateByPrimaryKeySelective(pmsBrand);
    }

    @Override
    public int deleteBrand(Long id) {
        return brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteBrand(List<Long> ids) {
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.createCriteria().andIdIn(ids);
        return brandMapper.deleteByExample(pmsBrandExample);
    }

    @Override
    public List<PmsBrand> listBrand(String keyword, Integer showStatus, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.setOrderByClause("sort desc");
        PmsBrandExample.Criteria criteria = pmsBrandExample.createCriteria();
        if (!StrUtil.isEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        if(showStatus!=null){
            criteria.andShowStatusEqualTo(showStatus);
        }
        return brandMapper.selectByExample(pmsBrandExample);
    }

    @Override
    public PmsBrand getBrand(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        PmsBrand pmsBrand = new PmsBrand();
        pmsBrand.setShowStatus(showStatus);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.createCriteria().andIdIn(ids);
        return brandMapper.updateByExampleSelective(pmsBrand, pmsBrandExample);
    }

    @Override
    public int updateFactoryStatus(List<Long> ids, Integer factoryStatus) {
        PmsBrand pmsBrand = new PmsBrand();
        pmsBrand.setFactoryStatus(factoryStatus);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.createCriteria().andIdIn(ids);
        return brandMapper.updateByExampleSelective(pmsBrand, pmsBrandExample);
    }

    @Override
    public int updateBrandForFrontend(Long id, String name, String subTitle, String logoUrl) {
        PmsBrand brand = new PmsBrand();
        brand.setId(id);
        
        // 设置品牌名称
        if (name != null && !name.trim().isEmpty()) {
            brand.setName(name.trim());
            // 如果更新名称，同时更新首字母
            brand.setFirstLetter(name.trim().substring(0, 1).toUpperCase());
        }
        
        // 设置子标题
        if (subTitle != null && !subTitle.trim().isEmpty()) {
            brand.setSubTitle(subTitle.trim());
        }
        
        // 设置Logo URL
        if (logoUrl != null && !logoUrl.trim().isEmpty()) {
            brand.setLogo(logoUrl.trim());
        }
        
        return brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public Long createBrandForFrontend(String name, String subTitle, String logoUrl, String firstLetter) {
        // 验证必填参数
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("品牌名称不能为空");
        }
        
        PmsBrand brand = new PmsBrand();
        brand.setName(name.trim());
        
        // 设置可选参数
        if (subTitle != null && !subTitle.trim().isEmpty()) {
            brand.setSubTitle(subTitle.trim());
        }
        if (logoUrl != null && !logoUrl.trim().isEmpty()) {
            brand.setLogo(logoUrl.trim());
        }
        
        // 设置首字母
        if (firstLetter != null && !firstLetter.trim().isEmpty()) {
            brand.setFirstLetter(firstLetter.trim().toUpperCase());
        } else {
            // 如果没有提供首字母，从品牌名称中提取
            brand.setFirstLetter(name.trim().substring(0, 1).toUpperCase());
        }
        
        // 设置默认值
        brand.setSort(0);
        brand.setFactoryStatus(1); // 默认为品牌制造商
        brand.setShowStatus(1);    // 默认显示
        brand.setProductCount(0);
        brand.setProductCommentCount(0);
        brand.setBrandStory("");
        
        // 插入数据库
        int result = brandMapper.insertSelective(brand);
        if (result > 0) {
            return brand.getId();
        } else {
            throw new RuntimeException("创建品牌失败");
        }
    }
}
