package com.macro.mall.service.impl;

import com.macro.mall.mapper.CmsBannerMapper;
import com.macro.mall.model.CmsBanner;
import com.macro.mall.service.CmsBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图管理Service实现类
 * Created on 2025-10-12
 */
@Service
public class CmsBannerServiceImpl implements CmsBannerService {

    @Autowired
    private CmsBannerMapper bannerMapper;

    @Override
    public List<CmsBanner> list(Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        return bannerMapper.selectByPage(offset, pageSize);
    }

    @Override
    public Long count() {
        return bannerMapper.countTotal();
    }

    @Override
    public CmsBanner getById(Long id) {
        return bannerMapper.selectById(id);
    }

    @Override
    public int create(CmsBanner banner) {
        // 设置默认值
        if (banner.getSort() == null) {
            banner.setSort(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        return bannerMapper.insert(banner);
    }

    @Override
    public int update(Long id, CmsBanner banner) {
        banner.setId(id);
        return bannerMapper.updateById(banner);
    }

    @Override
    public int delete(Long id) {
        return bannerMapper.deleteById(id);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        return bannerMapper.updateStatus(id, status);
    }
}