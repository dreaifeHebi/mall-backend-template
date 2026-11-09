package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.CmsBannerMapper;
import com.macro.mall.model.CmsBanner;
import com.macro.mall.portal.service.PortalBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Portal端轮播图Service实现类
 * Created on 2025-10-12
 */
@Service
public class PortalBannerServiceImpl implements PortalBannerService {

    @Autowired
    private CmsBannerMapper bannerMapper;

    @Override
    public List<CmsBanner> getEnabledBanners() {
        return bannerMapper.selectEnabledBanners();
    }
}