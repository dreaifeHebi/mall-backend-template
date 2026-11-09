package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.mapper.SmsEmailTemplateMapper;
import com.macro.mall.model.SmsEmailTemplate;
import com.macro.mall.model.SmsEmailTemplateExample;
import com.macro.mall.service.SmsEmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 邮件模板管理Service实现类
 * Created by mall on 2024/06/22.
 */
@Service
public class SmsEmailTemplateServiceImpl implements SmsEmailTemplateService {

    @Autowired
    private SmsEmailTemplateMapper smsEmailTemplateMapper;

    @Override
    public CommonPage<SmsEmailTemplate> list(Integer pageNum, Integer pageSize, String templateName,
                                           Integer triggerScene, Integer status) {
        // 使用PageHelper分页
        PageHelper.startPage(pageNum, pageSize);
        List<SmsEmailTemplate> templateList = smsEmailTemplateMapper.selectByPage(templateName, triggerScene, status);
        
        // 使用PageInfo获取分页信息
        PageInfo<SmsEmailTemplate> pageInfo = new PageInfo<>(templateList);
        
        CommonPage<SmsEmailTemplate> result = new CommonPage<>();
        result.setList(pageInfo.getList());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setTotalPage(pageInfo.getPages());
        
        return result;
    }

    @Override
    public SmsEmailTemplate getItem(Long id) {
        return smsEmailTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public int create(SmsEmailTemplate smsEmailTemplate) {
        smsEmailTemplate.setCreateTime(new Date());
        smsEmailTemplate.setUpdateTime(new Date());
        return smsEmailTemplateMapper.insertSelective(smsEmailTemplate);
    }

    @Override
    public int update(Long id, SmsEmailTemplate smsEmailTemplate) {
        smsEmailTemplate.setId(id);
        smsEmailTemplate.setUpdateTime(new Date());
        return smsEmailTemplateMapper.updateByPrimaryKeySelective(smsEmailTemplate);
    }

    @Override
    public int delete(Long id) {
        return smsEmailTemplateMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        SmsEmailTemplateExample example = new SmsEmailTemplateExample();
        example.createCriteria().andIdIn(ids);
        return smsEmailTemplateMapper.deleteByExample(example);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        SmsEmailTemplate template = new SmsEmailTemplate();
        template.setId(id);
        template.setStatus(status);
        template.setUpdateTime(new Date());
        return smsEmailTemplateMapper.updateByPrimaryKeySelective(template);
    }

    @Override
    public List<SmsEmailTemplate> getActiveTemplatesByScene(Integer triggerScene) {
        return smsEmailTemplateMapper.selectByTriggerSceneAndStatus(triggerScene, 1);
    }
}
