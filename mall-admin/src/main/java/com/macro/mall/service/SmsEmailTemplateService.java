package com.macro.mall.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.model.SmsEmailTemplate;

import java.util.List;

/**
 * 邮件模板管理Service
 * Created by mall on 2024/06/22.
 */
public interface SmsEmailTemplateService {

    /**
     * 分页查询邮件模板
     */
    CommonPage<SmsEmailTemplate> list(Integer pageNum, Integer pageSize, String templateName, 
                                     Integer triggerScene, Integer status);

    /**
     * 根据ID获取邮件模板详情
     */
    SmsEmailTemplate getItem(Long id);

    /**
     * 创建邮件模板
     */
    int create(SmsEmailTemplate smsEmailTemplate);

    /**
     * 更新邮件模板
     */
    int update(Long id, SmsEmailTemplate smsEmailTemplate);

    /**
     * 删除邮件模板
     */
    int delete(Long id);

    /**
     * 批量删除邮件模板
     */
    int delete(List<Long> ids);

    /**
     * 更新邮件模板状态
     */
    int updateStatus(Long id, Integer status);

    /**
     * 根据触发场景获取可用的邮件模板
     */
    List<SmsEmailTemplate> getActiveTemplatesByScene(Integer triggerScene);
}
