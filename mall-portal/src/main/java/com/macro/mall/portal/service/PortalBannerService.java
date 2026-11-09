package com.macro.mall.portal.service;

import com.macro.mall.model.CmsBanner;
import java.util.List;

/**
 * Portal端轮播图Service
 * Created on 2025-10-12
 */
public interface PortalBannerService {

    /**
     * 获取所有启用的轮播图
     */
    List<CmsBanner> getEnabledBanners();
}