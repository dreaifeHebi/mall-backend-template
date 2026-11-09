package com.macro.mall.portal.service;

import com.macro.mall.model.UmsMember;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员管理Service
 * Created by macro on 2018/8/3.
 */
public interface UmsMemberService {
    /**
     * 根据用户名获取会员
     */
    UmsMember getByUsername(String username);

    /**
     * 根据会员编号获取会员
     */
    UmsMember getById(Long id);

    /**
     * 用户注册
     */
    @Transactional
    void register(String username, String password, String telephone, String email);

    /**
     * 获取当前登录会员
     */
    UmsMember getCurrentMember();

    /**
     * 根据会员id修改会员积分
     */
    void updateIntegration(Long id,Integer integration);


    /**
     * 获取用户信息
     */
    UserDetails loadUserByUsername(String username);

    /**
     * 登录后获取token
     */
    String login(String username, String password);

    /**
     * 刷新token
     */
    String refreshToken(String token);

    /**
     * 发送重置密码邮件
     */
    void sendResetEmail(String email);

    /**
     * 使用token重置密码
     */
    @Transactional
    void resetPassword(String token, String newPassword);

    /**
     * 修改密码（需要原密码验证）
     */
    @Transactional
    void changePassword(String oldPassword, String newPassword);

    /**
     * 发送邮箱验证邮件
     */
    void sendEmailVerification(String email);

    /**
     * 验证邮箱确认token并激活账户
     */
    @Transactional
    void confirmEmail(String token);

    /**
     * 根据邮箱地址查找用户
     */
    UmsMember getByEmail(String email);

    /**
     * 更新会员信息（昵称和电话号码）
     */
    @Transactional
    void updateMemberInfo(String nickname, String phone);
}
