package com.macro.mall.service;

import com.macro.mall.model.UmsPermission;
import java.util.List;

/**
 * 商城用户权限管理Service
 * Created by mall on 2025/06/22.
 */
public interface MallUserPermissionService {
    
    /**
     * 获取角色的权限列表
     */
    List<UmsPermission> getPermissionsByRoleId(Long roleId);
    
    /**
     * 为角色分配权限
     */
    int allocPermissionsToRole(Long roleId, List<Long> permissionIds);
    
    /**
     * 获取所有商城用户管理相关权限
     */
    List<UmsPermission> getMallUserPermissions();
    
    /**
     * 验证用户是否有指定权限
     */
    boolean hasPermission(Long userId, String permissionValue);
    
    /**
     * 获取用户的所有权限
     */
    List<UmsPermission> getUserPermissions(Long userId);
}
