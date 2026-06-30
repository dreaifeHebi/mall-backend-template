package com.macro.mall.service;

import com.macro.mall.model.CmsBanner;
import java.util.List;

/**
 * 轮播图管理Service
 * Created on 2025-10-12
 */
public interface CmsBannerService {

    /**
     * 分页查询轮播图
     */
    List<CmsBanner> list(Integer pageNum, Integer pageSize);

    /**
     * 获取轮播图总数
     */
    Long count();

    /**
     * 根据ID获取轮播图
     */
    CmsBanner getById(Long id);

    /**
     * 创建轮播图
     */
    int create(CmsBanner banner);

    /**
     * 更新轮播图
     */
    int update(Long id, CmsBanner banner);

    /**
     * 删除轮播图
     */
    int delete(Long id);

    /**
     * 更新状态
     */
    int updateStatus(Long id, Integer status);
}