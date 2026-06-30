package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.SysSettingParam;
import com.macro.mall.dto.SysSettingQueryParam;
import com.macro.mall.model.SysSetting;
import com.macro.mall.service.SysSettingService;
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
 * 系统设置管理Controller
 */
@RestController
@Api(tags = "SysSettingController")
@Tag(name = "SysSettingController", description = "系统设置管理")
@RequestMapping("/sysSetting")
public class SysSettingController {

    @Autowired
    private SysSettingService sysSettingService;

    @ApiOperation("分页查询系统设置")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SysSetting>> list(SysSettingQueryParam queryParam) {
        return CommonResult.success(sysSettingService.list(queryParam));
    }

    @ApiOperation("根据ID获取系统设置")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CommonResult<SysSetting> getItem(@PathVariable Long id) {
        return CommonResult.success(sysSettingService.getItem(id));
    }

    @ApiOperation("根据设置键获取设置值")
    @RequestMapping(value = "/getValue/{settingKey}", method = RequestMethod.GET)
    public CommonResult<String> getValue(@PathVariable String settingKey) {
        return CommonResult.success(sysSettingService.getValue(settingKey));
    }

    @ApiOperation("获取所有启用设置")
    @RequestMapping(value = "/getAllSettings", method = RequestMethod.GET)
    public CommonResult<Map<String, String>> getAllSettings() {
        return CommonResult.success(sysSettingService.getAllSettings());
    }

    @ApiOperation("创建系统设置")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Integer> create(@Validated @RequestBody SysSettingParam sysSettingParam) {
        SysSetting sysSetting = new SysSetting();
        BeanUtils.copyProperties(sysSettingParam, sysSetting);
        int count = sysSettingService.create(sysSetting);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("设置键已存在或保存失败");
    }

    @ApiOperation("更新系统设置")
    @RequestMapping(value = "/update/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    public CommonResult<Integer> update(@PathVariable Long id,
                                        @Validated @RequestBody SysSettingParam sysSettingParam) {
        SysSetting sysSetting = new SysSetting();
        BeanUtils.copyProperties(sysSettingParam, sysSetting);
        int count = sysSettingService.update(id, sysSetting);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("删除系统设置")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public CommonResult<Integer> delete(@PathVariable Long id) {
        int count = sysSettingService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除系统设置")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    public CommonResult<Integer> deleteBatch(@RequestBody List<Long> ids) {
        int count = sysSettingService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("更新系统设置状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public CommonResult<Integer> updateStatus(@RequestBody Map<String, Object> param) {
        Long id = Long.valueOf(String.valueOf(param.get("id")));
        Integer status = Integer.valueOf(String.valueOf(param.get("status")));
        int count = sysSettingService.updateStatus(id, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量更新系统设置值")
    @RequestMapping(value = "/updateBatch", method = RequestMethod.POST)
    public CommonResult<Integer> updateBatch(@RequestBody Map<String, String> settings) {
        int count = sysSettingService.updateBatch(settings);
        return CommonResult.success(count);
    }
}
