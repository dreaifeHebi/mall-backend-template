package com.macro.mall.service;

import com.macro.mall.dto.*;

import java.util.List;

/**
 * 商城用户管理Service
 * Created by mall on 2025/06/22.
 */
public interface MallUserService {

    /**
     * 获取商城用户列表
     * @param queryParam 查询参数
     * @return 用户列表
     */
    List<MallUserDetailDto> list(MallUserQueryParam queryParam);

    /**
     * 获取商城用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    MallUserDetailDto getDetail(Long id);

    /**
     * 更新商城用户信息
     * @param id 用户ID
     * @param updateParam 更新参数
     * @return 更新结果
     */
    int update(Long id, MallUserUpdateParam updateParam);

    /**
     * 更新商城用户状态
     * @param id 用户ID
     * @param status 状态值
     * @return 更新结果
     */
    int updateStatus(Long id, Integer status);

    /**
     * 批量冻结商城用户
     * @param ids 用户ID列表
     * @return 操作结果
     */
    int batchFreeze(List<Long> ids);

    /**
     * 删除商城用户
     * @param id 用户ID
     * @return 删除结果
     */
    int delete(Long id);

    /**
     * 获取商城用户角色列表
     * @return 角色列表
     */
    List<MallUserRoleDto> getRoles();
}
