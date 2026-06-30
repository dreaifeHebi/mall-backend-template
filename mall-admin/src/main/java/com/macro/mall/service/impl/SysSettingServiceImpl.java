package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.dto.SysSettingQueryParam;
import com.macro.mall.mapper.SysSettingMapper;
import com.macro.mall.model.SysSetting;
import com.macro.mall.model.SysSettingExample;
import com.macro.mall.service.SysSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置管理Service实现类
 */
@Service
public class SysSettingServiceImpl implements SysSettingService {

    @Autowired
    private SysSettingMapper sysSettingMapper;

    @Override
    public CommonPage<SysSetting> list(SysSettingQueryParam queryParam) {
        PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        SysSettingExample example = new SysSettingExample();
        example.setOrderByClause("id desc");
        SysSettingExample.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(queryParam.getSettingKey())) {
            criteria.andSettingKeyLike("%" + queryParam.getSettingKey().trim() + "%");
        }
        if (StrUtil.isNotBlank(queryParam.getSettingName())) {
            criteria.andSettingNameLike("%" + queryParam.getSettingName().trim() + "%");
        }
        if (queryParam.getType() != null) {
            criteria.andTypeEqualTo(queryParam.getType());
        }
        if (queryParam.getStatus() != null) {
            criteria.andStatusEqualTo(queryParam.getStatus());
        }
        return CommonPage.restPage(sysSettingMapper.selectByExample(example));
    }

    @Override
    public SysSetting getItem(Long id) {
        return sysSettingMapper.selectByPrimaryKey(id);
    }

    @Override
    public String getValue(String settingKey) {
        SysSetting setting = getByKey(settingKey);
        return setting == null ? null : setting.getSettingValue();
    }

    @Override
    public Map<String, String> getAllSettings() {
        SysSettingExample example = new SysSettingExample();
        example.createCriteria().andStatusEqualTo(1);
        example.setOrderByClause("id asc");
        List<SysSetting> settings = sysSettingMapper.selectByExample(example);
        Map<String, String> result = new HashMap<>();
        for (SysSetting setting : settings) {
            result.put(setting.getSettingKey(), setting.getSettingValue());
        }
        return result;
    }

    @Override
    public int create(SysSetting sysSetting) {
        if (getByKey(sysSetting.getSettingKey()) != null) {
            return 0;
        }
        Date now = new Date();
        sysSetting.setCreateTime(now);
        sysSetting.setUpdateTime(now);
        if (sysSetting.getType() == null) {
            sysSetting.setType(1);
        }
        if (sysSetting.getStatus() == null) {
            sysSetting.setStatus(1);
        }
        return sysSettingMapper.insertSelective(sysSetting);
    }

    @Override
    public int update(Long id, SysSetting sysSetting) {
        sysSetting.setId(id);
        sysSetting.setUpdateTime(new Date());
        return sysSettingMapper.updateByPrimaryKeySelective(sysSetting);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        SysSetting sysSetting = new SysSetting();
        sysSetting.setId(id);
        sysSetting.setStatus(status);
        sysSetting.setUpdateTime(new Date());
        return sysSettingMapper.updateByPrimaryKeySelective(sysSetting);
    }

    @Override
    public int updateBatch(Map<String, String> settings) {
        if (settings == null || settings.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            SysSetting existing = getByKey(entry.getKey());
            if (existing == null) {
                continue;
            }
            SysSetting sysSetting = new SysSetting();
            sysSetting.setId(existing.getId());
            sysSetting.setSettingValue(entry.getValue());
            sysSetting.setUpdateTime(new Date());
            count += sysSettingMapper.updateByPrimaryKeySelective(sysSetting);
        }
        return count;
    }

    @Override
    public int delete(Long id) {
        return sysSettingMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        SysSettingExample example = new SysSettingExample();
        example.createCriteria().andIdIn(ids);
        return sysSettingMapper.deleteByExample(example);
    }

    private SysSetting getByKey(String settingKey) {
        if (StrUtil.isBlank(settingKey)) {
            return null;
        }
        SysSettingExample example = new SysSettingExample();
        example.createCriteria().andSettingKeyEqualTo(settingKey);
        List<SysSetting> settings = sysSettingMapper.selectByExample(example);
        return settings.isEmpty() ? null : settings.get(0);
    }
}
