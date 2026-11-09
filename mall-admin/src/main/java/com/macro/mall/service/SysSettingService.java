package com.macro.mall.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.dto.SysSettingQueryParam;
import com.macro.mall.model.SysSetting;

import java.util.List;
import java.util.Map;

/**
 * 系统设置管理Service
 */
public interface SysSettingService {

    CommonPage<SysSetting> list(SysSettingQueryParam queryParam);

    SysSetting getItem(Long id);

    String getValue(String settingKey);

    Map<String, String> getAllSettings();

    int create(SysSetting sysSetting);

    int update(Long id, SysSetting sysSetting);

    int updateStatus(Long id, Integer status);

    int updateBatch(Map<String, String> settings);

    int delete(Long id);

    int delete(List<Long> ids);
}
