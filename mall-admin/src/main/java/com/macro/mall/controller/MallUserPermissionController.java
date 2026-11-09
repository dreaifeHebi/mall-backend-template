package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.UmsRoleService;
import com.macro.mall.service.MallUserPermissionService;
import com.macro.mall.model.UmsRole;
import com.macro.mall.model.UmsPermission;
import com.macro.mall.model.UmsResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商城用户权限管理Controller
 * Created by mall on 2025/06/22.
 */
@Controller
@Api(tags = "MallUserPermissionController")
@Tag(name = "MallUserPermissionController", description = "商城用户权限管理")
@RequestMapping("/mallUserPermission")
public class MallUserPermissionController {    @Autowired
    private UmsRoleService roleService;

    @Autowired
    private MallUserPermissionService mallUserPermissionService;

    @ApiOperation("获取所有角色列表")
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ums:role:read')")
    public CommonResult<List<UmsRole>> getAllRoles() {
        List<UmsRole> roles = roleService.list();
        return CommonResult.success(roles);
    }

    @ApiOperation("获取角色的权限列表")
    @RequestMapping(value = "/role/{roleId}/permissions", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ums:role:read')")
    public CommonResult<List<UmsPermission>> getRolePermissions(@PathVariable Long roleId) {
        List<UmsPermission> permissions = mallUserPermissionService.getPermissionsByRoleId(roleId);
        return CommonResult.success(permissions);
    }

    @ApiOperation("为角色分配权限")
    @RequestMapping(value = "/role/{roleId}/allocPermissions", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ums:role:allocPermission')")
    public CommonResult<Object> allocRolePermissions(@PathVariable Long roleId, @RequestParam List<Long> permissionIds) {
        int count = mallUserPermissionService.allocPermissionsToRole(roleId, permissionIds);
        if (count > 0) {
            return CommonResult.success(null, "权限分配成功");
        } else {
            return CommonResult.failed("权限分配失败");
        }
    }

    @ApiOperation("获取所有商城用户权限")
    @RequestMapping(value = "/mallUserPermissions", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ums:role:read')")
    public CommonResult<List<UmsPermission>> getMallUserPermissions() {
        List<UmsPermission> permissions = mallUserPermissionService.getMallUserPermissions();
        return CommonResult.success(permissions);
    }

    @ApiOperation("获取角色的资源列表")
    @RequestMapping(value = "/role/{roleId}/resources", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ums:role:read')")
    public CommonResult<List<UmsResource>> getRoleResources(@PathVariable Long roleId) {
        List<UmsResource> resources = roleService.listResource(roleId);
        return CommonResult.success(resources);
    }

    @ApiOperation("为角色分配资源")
    @RequestMapping(value = "/role/{roleId}/allocResources", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ums:role:allocResource')")
    public CommonResult<Object> allocRoleResources(@PathVariable Long roleId, @RequestParam List<Long> resourceIds) {
        int count = roleService.allocResource(roleId, resourceIds);
        if (count > 0) {
            return CommonResult.success(null, "资源分配成功");
        } else {
            return CommonResult.failed("资源分配失败");
        }
    }

    @ApiOperation("商城用户权限矩阵说明")
    @RequestMapping(value = "/matrix", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String> getPermissionMatrix() {
        String matrix = "商城用户管理权限矩阵:\n\n" +
                "1. 普通管理员 (ID: 6):\n" +
                "   - mall:user:read (查看用户列表)\n" +
                "   - mall:user:detail (查看用户详情)\n" +
                "   - mall:user:role_read (查看用户角色)\n" +
                "   - 无修改、删除权限\n\n" +
                "2. 高级管理员 (ID: 7):\n" +
                "   - mall:user:read (查看用户列表)\n" +
                "   - mall:user:detail (查看用户详情)\n" +
                "   - mall:user:update (更新用户信息)\n" +
                "   - mall:user:status (管理用户状态)\n" +
                "   - mall:user:delete (删除用户)\n" +
                "   - mall:user:batch_freeze (批量冻结)\n" +
                "   - mall:user:role_read (查看用户角色)\n\n" +
                "3. 超级管理员 (ID: 5):\n" +
                "   - 拥有所有权限\n" +
                "   - 可以管理角色和权限分配\n\n" +
                "注意：权限控制通过 @PreAuthorize 注解实现，确保数据库中已正确配置角色-权限关系。";
        
        return CommonResult.success(matrix);
    }
}
