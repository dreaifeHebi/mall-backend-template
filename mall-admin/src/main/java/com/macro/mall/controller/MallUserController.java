package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.service.MallUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 商城用户管理Controller
 * Created by mall on 2025/06/22.
 */
@Controller
@Api(tags = "MallUserController")
@Tag(name = "MallUserController", description = "商城用户管理")
@RequestMapping("/mallUser")
public class MallUserController {

    @Autowired
    private MallUserService mallUserService;    @ApiOperation("获取商城用户列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('mall:user:read')")
    public CommonResult<CommonPage<MallUserDetailDto>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status) {
        
        MallUserQueryParam queryParam = new MallUserQueryParam();
        queryParam.setPageNum(pageNum);
        queryParam.setPageSize(pageSize);
        queryParam.setKeyword(keyword);
        queryParam.setStatus(status);
        
        List<MallUserDetailDto> userList = mallUserService.list(queryParam);
        return CommonResult.success(CommonPage.restPage(userList));
    }

    @ApiOperation("获取商城用户详情")    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('mall:user:detail')")
    public CommonResult<MallUserDetailDto> getDetail(@PathVariable Long id) {
        MallUserDetailDto user = mallUserService.getDetail(id);
        if (user == null) {
            return CommonResult.failed("用户不存在");
        }
        return CommonResult.success(user);
    }

    @ApiOperation("更新商城用户信息")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('mall:user:update')")
    public CommonResult<Object> update(@PathVariable Long id, @Valid @RequestBody MallUserUpdateParam updateParam) {
        int count = mallUserService.update(id, updateParam);
        if (count > 0) {
            return CommonResult.success(null, "更新成功");
        } else {
            return CommonResult.failed("更新失败");
        }
    }

    @ApiOperation("更新商城用户状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('mall:user:status')")
    public CommonResult<Object> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        int count = mallUserService.updateStatus(id, status);
        if (count > 0) {
            return CommonResult.success(null, "状态更新成功");
        } else {
            return CommonResult.failed("状态更新失败");
        }
    }    @ApiOperation("批量冻结商城用户")
    @RequestMapping(value = "/batchFreeze", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('mall:user:batch_freeze')")
    public CommonResult<Object> batchFreeze(@Valid @RequestBody BatchOperationParam param) {
        int count = mallUserService.batchFreeze(param.getIds());
        if (count > 0) {
            return CommonResult.success(null, "批量冻结成功");
        } else {
            return CommonResult.failed("批量冻结失败");
        }
    }

    @ApiOperation("删除商城用户")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('mall:user:delete')")
    public CommonResult<Object> delete(@PathVariable Long id) {
        int count = mallUserService.delete(id);
        if (count > 0) {
            return CommonResult.success(null, "删除成功");
        } else {
            return CommonResult.failed("删除失败");
        }
    }

    @ApiOperation("获取商城用户角色列表")
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('mall:user:role_read')")
    public CommonResult<List<MallUserRoleDto>> getRoles() {
        List<MallUserRoleDto> roles = mallUserService.getRoles();
        return CommonResult.success(roles);
    }
}
