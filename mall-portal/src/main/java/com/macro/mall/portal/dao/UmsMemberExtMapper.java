package com.macro.mall.portal.dao;

import com.macro.mall.model.UmsMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * UmsMember 扩展Mapper接口
 * 用于避免与MyBatis Generator自动生成的代码冲突
 */
public interface UmsMemberExtMapper {
    
    /**
     * 根据邮箱地址查找用户
     * @param email 邮箱地址
     * @return 用户信息，如果不存在则返回null
     */
    @Select("SELECT id, member_level_id, username, password, nickname, phone, email, email_verified, status, create_time, icon, gender, birthday, city, job, personalized_signature, source_type, integration, growth, luckey_count, history_integration FROM ums_member WHERE email = #{email}")
    UmsMember selectByEmail(@Param("email") String email);
}
