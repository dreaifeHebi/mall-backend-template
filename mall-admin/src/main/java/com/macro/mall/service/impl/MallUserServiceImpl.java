package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.service.MallUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商城用户管理Service实现类
 * Created by mall on 2025/06/22.
 */
@Service
public class MallUserServiceImpl implements MallUserService {

    @Autowired
    private UmsMemberMapper memberMapper;

    @Override
    public List<MallUserDetailDto> list(MallUserQueryParam queryParam) {
        PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        UmsMemberExample example = new UmsMemberExample();

        if (StrUtil.isNotEmpty(queryParam.getKeyword())) {
            String keyword = "%" + queryParam.getKeyword() + "%";
            UmsMemberExample.Criteria criteria1 = example.createCriteria();
            criteria1.andUsernameLike(keyword);
            UmsMemberExample.Criteria criteria2 = example.createCriteria();
            criteria2.andPhoneLike(keyword);
            UmsMemberExample.Criteria criteria3 = example.createCriteria();
            criteria3.andNicknameLike(keyword);
            UmsMemberExample.Criteria criteria4 = example.createCriteria();
            criteria4.andEmailLike(keyword);
            example.or(criteria1);
            example.or(criteria2);
            example.or(criteria3);
            example.or(criteria4);
        } else {
            example.createCriteria();
        }

        if (queryParam.getStatus() != null) {
            for (UmsMemberExample.Criteria criteria : example.getOredCriteria()) {
                criteria.andStatusEqualTo(queryParam.getStatus());
            }
        }

        example.setOrderByClause("create_time desc");
        
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        return memberList.stream().map(this::convertToDetailDto).collect(Collectors.toList());
    }

    @Override
    public MallUserDetailDto getDetail(Long id) {
        UmsMember member = memberMapper.selectByPrimaryKey(id);
        if (member == null) {
            return null;
        }
        return convertToDetailDto(member);
    }

    @Override
    public int update(Long id, MallUserUpdateParam updateParam) {
        UmsMember member = new UmsMember();
        BeanUtils.copyProperties(updateParam, member);
        member.setId(id);
        member.setIcon(updateParam.getAvatar());
        return memberMapper.updateByPrimaryKeySelective(member);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        UmsMember member = new UmsMember();
        member.setId(id);
        member.setStatus(status);
        return memberMapper.updateByPrimaryKeySelective(member);
    }    @Override
    public int batchFreeze(List<Long> ids) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andIdIn(ids);
        
        UmsMember record = new UmsMember();
        record.setStatus(0); // 0表示禁用/冻结状态
        
        return memberMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int delete(Long id) {
        return memberMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<MallUserRoleDto> getRoles() {
        // 模拟用户角色数据，实际项目中应该从数据库查询
        List<MallUserRoleDto> roles = new ArrayList<>();
        
        MallUserRoleDto role1 = new MallUserRoleDto();
        role1.setId(1L);
        role1.setName("普通用户");
        role1.setDescription("普通会员用户");
        roles.add(role1);
        
        MallUserRoleDto role2 = new MallUserRoleDto();
        role2.setId(2L);
        role2.setName("VIP用户");
        role2.setDescription("VIP会员用户");
        roles.add(role2);
        
        MallUserRoleDto role3 = new MallUserRoleDto();
        role3.setId(3L);
        role3.setName("超级VIP");
        role3.setDescription("超级VIP会员用户");
        roles.add(role3);
        
        return roles;
    }

    /**
     * 将UmsMember转换为MallUserDetailDto
     */
    private MallUserDetailDto convertToDetailDto(UmsMember member) {
        MallUserDetailDto dto = new MallUserDetailDto();
        BeanUtils.copyProperties(member, dto);
        
        // 设置头像字段（UmsMember中是icon字段）
        dto.setAvatar(member.getIcon());

        // 当前模板尚未维护会员登录日志聚合字段，最后登录时间保留为空。
        dto.setLoginTime(null);
        
        return dto;
    }
}
