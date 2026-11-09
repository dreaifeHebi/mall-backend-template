package com.macro.mall.service.impl;

import com.macro.mall.mapper.UmsPermissionMapper;
import com.macro.mall.mapper.UmsRolePermissionRelationMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.MallUserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商城用户权限管理Service实现类
 * Created by mall on 2025/06/22.
 */
@Service
public class MallUserPermissionServiceImpl implements MallUserPermissionService {

    @Autowired
    private UmsPermissionMapper permissionMapper;
    
    @Autowired
    private UmsRolePermissionRelationMapper rolePermissionRelationMapper;

    @Override
    public List<UmsPermission> getPermissionsByRoleId(Long roleId) {
        // 查询角色权限关系
        UmsRolePermissionRelationExample example = new UmsRolePermissionRelationExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        List<UmsRolePermissionRelation> relations = rolePermissionRelationMapper.selectByExample(example);
        
        if (relations.isEmpty()) {
            return null;
        }
        
        // 获取权限ID列表
        List<Long> permissionIds = relations.stream()
                .map(UmsRolePermissionRelation::getPermissionId)
                .collect(Collectors.toList());
        
        // 查询权限详情
        UmsPermissionExample permissionExample = new UmsPermissionExample();
        permissionExample.createCriteria().andIdIn(permissionIds);
        return permissionMapper.selectByExample(permissionExample);
    }

    @Override
    @Transactional
    public int allocPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 先删除原有权限关系
        UmsRolePermissionRelationExample example = new UmsRolePermissionRelationExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        rolePermissionRelationMapper.deleteByExample(example);
        
        // 批量插入新的权限关系
        for (Long permissionId : permissionIds) {
            UmsRolePermissionRelation relation = new UmsRolePermissionRelation();
            relation.setRoleId(roleId);
            relation.setPermissionId(permissionId);
            rolePermissionRelationMapper.insert(relation);
        }
        
        return permissionIds.size();
    }

    @Override
    public List<UmsPermission> getMallUserPermissions() {
        // 查询所有商城用户管理相关权限（权限值以mall:user开头）
        UmsPermissionExample example = new UmsPermissionExample();
        example.createCriteria().andValueLike("mall:user%");
        return permissionMapper.selectByExample(example);
    }

    @Override
    public boolean hasPermission(Long userId, String permissionValue) {
        // 这里需要根据用户ID查询其角色，再查询角色权限
        // 为简化实现，此处返回基本逻辑
        // 实际项目中需要完整实现用户-角色-权限的查询链
        return false; // 实际实现需要查询用户角色权限
    }

    @Override
    public List<UmsPermission> getUserPermissions(Long userId) {
        // 这里需要根据用户ID查询其所有权限
        // 实际项目中需要查询用户的所有角色，再获取角色的所有权限
        return null; // 实际实现需要完整的用户权限查询
    }
}
