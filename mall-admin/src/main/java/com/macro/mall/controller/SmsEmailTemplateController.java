package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.SmsEmailTemplateParam;
import com.macro.mall.model.SmsEmailTemplate;
import com.macro.mall.service.SmsEmailTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 邮件模板管理Controller
 * Created by mall on 2024/06/22.
 */
@RestController
@Api(tags = "SmsEmailTemplateController")
@Tag(name = "SmsEmailTemplateController", description = "邮件模板管理")
@RequestMapping("/smsEmailTemplate")
public class SmsEmailTemplateController {

    @Autowired
    private SmsEmailTemplateService smsEmailTemplateService;

    @ApiOperation("分页查询邮件模板")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SmsEmailTemplate>> list(
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "templateName", required = false) String templateName,
            @RequestParam(value = "triggerScene", required = false) Integer triggerScene,
            @RequestParam(value = "status", required = false) Integer status) {
        CommonPage<SmsEmailTemplate> result = smsEmailTemplateService.list(pageNum, pageSize, 
                                                                          templateName, triggerScene, status);
        return CommonResult.success(result);
    }

    @ApiOperation("根据ID获取邮件模板详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CommonResult<SmsEmailTemplate> getItem(@PathVariable Long id) {
        SmsEmailTemplate template = smsEmailTemplateService.getItem(id);
        return CommonResult.success(template);
    }

    @ApiOperation("创建邮件模板")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Integer> create(@Validated @RequestBody SmsEmailTemplateParam templateParam) {
        SmsEmailTemplate template = new SmsEmailTemplate();
        BeanUtils.copyProperties(templateParam, template);
        int count = smsEmailTemplateService.create(template);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("更新邮件模板")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public CommonResult<Integer> update(@PathVariable Long id, @Validated @RequestBody SmsEmailTemplateParam templateParam) {
        SmsEmailTemplate template = new SmsEmailTemplate();
        BeanUtils.copyProperties(templateParam, template);
        int count = smsEmailTemplateService.update(id, template);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("删除邮件模板")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<Integer> delete(@PathVariable Long id) {
        int count = smsEmailTemplateService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除邮件模板")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    public CommonResult<Integer> deleteBatch(@RequestBody List<Long> ids) {
        int count = smsEmailTemplateService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("更新邮件模板状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public CommonResult<Integer> updateStatus(@RequestBody Map<String, Object> param) {
        Long id = Long.valueOf(String.valueOf(param.get("id")));
        Integer status = Integer.valueOf(String.valueOf(param.get("status")));
        int count = smsEmailTemplateService.updateStatus(id, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
