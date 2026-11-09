package com.macro.mall.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.portal.domain.MemberDetails;
import com.macro.mall.portal.service.UmsMemberCacheService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.security.util.JwtTokenUtil;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.service.EmailService;
import com.macro.mall.service.EmailNotificationService;
import com.macro.mall.portal.dao.UmsMemberExtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 会员管理Service实现类
 * Created by macro on 2018/8/3.
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private UmsMemberExtMapper memberExtMapper;
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    @Autowired
    private UmsMemberCacheService memberCacheService;
    @Autowired
    private RedisService redisService;
    @Value("${redis.key.authCode}")
    private String REDIS_KEY_PREFIX_AUTH_CODE;
    @Value("${redis.expire.authCode}")
    private Long AUTH_CODE_EXPIRE_SECONDS;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    @Value("${mall.frontend.base-url:http://localhost:8060}")
    private String frontendBaseUrl;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    @Qualifier("portalEmailNotificationService")
    private EmailNotificationService emailNotificationService;

    @Override
    public UmsMember getByUsername(String username) {
        UmsMember member = memberCacheService.getMember(username);
        if(member!=null) return member;
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(memberList)) {
            member = memberList.get(0);
            memberCacheService.setMember(member);
            return member;
        }
        return null;
    }

    @Override
    public UmsMember getById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public void register(String username, String password, String telephone, String email) {
        //查询是否已有该用户
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        // 只有当手机号不为空时才检查手机号是否已存在
        if (telephone != null && !telephone.trim().isEmpty()) {
            example.or(example.createCriteria().andPhoneEqualTo(telephone));
        }
        List<UmsMember> umsMembers = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(umsMembers)) {
            Asserts.fail("该用户已经存在");
        }
        //没有该用户进行添加操作
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        umsMember.setPhone(telephone);
        umsMember.setEmail(email);
        umsMember.setPassword(passwordEncoder.encode(password));
        umsMember.setCreateTime(new Date());
        umsMember.setStatus(1);
        umsMember.setEmailVerified(Integer.valueOf(0));
        
        // 生成用户昵称：TABA_年月日 + 5位随机字符
        umsMember.setNickname(generateNickname());
        //获取默认会员等级并设置
        UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
        levelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
        if (!CollectionUtils.isEmpty(memberLevelList)) {
            umsMember.setMemberLevelId(memberLevelList.get(0).getId());
        }
        memberMapper.insert(umsMember);
        umsMember.setPassword(null);
        
        // 发送注册确认邮件
        if (email != null && !email.trim().isEmpty()) {
            String verificationToken = UUID.randomUUID().toString();
            redisService.set("email_verification:" + verificationToken, umsMember.getId(), 24 * 60 * 60L);
            String confirmationLink = frontendBaseUrl + "/auth/email-confirm?token=" + verificationToken;
            emailNotificationService.sendRegistrationConfirmationEmail(umsMember.getId(), confirmationLink);
        }
    }

    @Override
    public UmsMember getCurrentMember() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("用户未登录");
        }
        
        Object principal = auth.getPrincipal();
        if (principal instanceof String) {
            // 如果是字符串，说明是匿名用户或认证有问题
            throw new RuntimeException("用户未正确认证，请重新登录");
        }
        
        if (!(principal instanceof MemberDetails)) {
            throw new RuntimeException("用户认证信息异常，请重新登录");
        }
        
        MemberDetails memberDetails = (MemberDetails) principal;
        return memberDetails.getUmsMember();
    }

    @Override
    public void updateIntegration(Long id, Integer integration) {
        UmsMember record=new UmsMember();
        record.setId(id);
        record.setIntegration(integration);
        memberMapper.updateByPrimaryKeySelective(record);
        memberCacheService.delMember(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UmsMember member = getByUsername(username);
        if(member!=null){
            return new MemberDetails(member);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public String refreshToken(String token) {
        return jwtTokenUtil.refreshHeadToken(token);
    }

    @Override
    public void sendResetEmail(String email) {
        // 通过邮箱地址查找用户
        UmsMember member = memberExtMapper.selectByEmail(email);
        
        if (member == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 生成重置token
        String resetToken = UUID.randomUUID().toString();
        
        // 将token存储到Redis，设置过期时间为24小时（模仿邮箱验证的方式）
        redisService.set("password_reset:" + resetToken, member.getId(), 24 * 60 * 60L);
        
        // 构建密码重置链接（模仿邮箱验证的方式）
        String resetLink = frontendBaseUrl + "/auth/reset-password?token=" + resetToken;
        
        try {
            // 使用和邮箱验证相同的邮件服务发送密码重置邮件
            emailNotificationService.sendRegistrationConfirmationEmail(member.getId(), resetLink);
            LOGGER.info("密码重置邮件已发送到: {}", email);
        } catch (Exception e) {
            LOGGER.error("发送密码重置邮件失败", e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (StrUtil.isEmpty(token) || StrUtil.isEmpty(newPassword)) {
            throw new RuntimeException("参数不能为空");
        }
        
        // 从Redis获取token对应的用户ID
        Object userIdObj = redisService.get("password_reset:" + token);
        if (userIdObj == null) {
            throw new RuntimeException("重置链接无效或已过期");
        }
        
        Long userId = Long.valueOf(userIdObj.toString());
        
        // 更新用户密码
        UmsMember updateMember = new UmsMember();
        updateMember.setId(userId);
        updateMember.setPassword(passwordEncoder.encode(newPassword));
        
        int result = memberMapper.updateByPrimaryKeySelective(updateMember);
        if (result > 0) {
            // 删除重置token
            redisService.del("password_reset:" + token);
            
            // 清除用户缓存
            memberCacheService.delMember(userId);
            
            LOGGER.info("用户 {} 密码重置成功", userId);
        } else {
            throw new RuntimeException("密码重置失败");
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        if (StrUtil.isEmpty(oldPassword) || StrUtil.isEmpty(newPassword)) {
            throw new RuntimeException("密码不能为空");
        }
        
        // 获取当前登录用户
        UmsMember currentMember = getCurrentMember();
        if (currentMember == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, currentMember.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        UmsMember updateMember = new UmsMember();
        updateMember.setId(currentMember.getId());
        updateMember.setPassword(passwordEncoder.encode(newPassword));
        
        int result = memberMapper.updateByPrimaryKeySelective(updateMember);
        if (result <= 0) {
            throw new RuntimeException("密码修改失败");
        }

        memberCacheService.delMember(currentMember.getId());
    }
    
    /**
     * 生成用户昵称：TABA_年月日 + 5位随机字符
     */
    private String generateNickname() {
        // 获取当前日期的年月日
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());
        
        // 生成5位随机字符（包含数字和字母）
        String randomStr = generateRandomString(5);
        
        return "TABA_" + dateStr + randomStr;
    }
    
    /**
     * 生成指定长度的随机字符串（包含数字和字母）
     */
    private String generateRandomString(int length) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public void sendEmailVerification(String email) {
        // 查找用户
        UmsMember member = memberExtMapper.selectByEmail(email);
        
        if (member == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查邮箱是否已验证
        if (member.getEmailVerified() != null && member.getEmailVerified() == 1) {
            throw new RuntimeException("邮箱已验证，无需重复验证");
        }
        
        // 生成验证token
        String verificationToken = UUID.randomUUID().toString();
        
        // 将token存储到Redis，设置过期时间为24小时
        redisService.set("email_verification:" + verificationToken, member.getId(), 24 * 60 * 60L);
        
        // 发送验证邮件
        String confirmationLink = frontendBaseUrl + "/auth/email-confirm?token=" + verificationToken;
        try {
            // 使用现有的邮件服务发送验证邮件
            emailNotificationService.sendRegistrationConfirmationEmail(member.getId(), confirmationLink);
            LOGGER.info("邮箱验证邮件已发送到: {}", email);
        } catch (Exception e) {
            LOGGER.error("发送邮箱验证邮件失败", e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void confirmEmail(String token) {
        // 从Redis获取token对应的用户ID
        Object userIdObj = redisService.get("email_verification:" + token);
        if (userIdObj == null) {
            throw new RuntimeException("验证链接无效或已过期");
        }
        
        Long userId = Long.valueOf(userIdObj.toString());
        
        // 更新用户邮箱验证状态
        UmsMember updateMember = new UmsMember();
        updateMember.setId(userId);
        updateMember.setEmailVerified(Integer.valueOf(1));
        
        int result = memberMapper.updateByPrimaryKeySelective(updateMember);
        if (result > 0) {
            // 删除验证token
            redisService.del("email_verification:" + token);
            
            // 清除用户缓存并重新设置
            memberCacheService.delMember(userId);
            
            // 从数据库重新获取用户信息并更新缓存
            UmsMember updatedMember = memberMapper.selectByPrimaryKey(userId);
            if (updatedMember != null) {
                memberCacheService.setMember(updatedMember);
            }
            
            LOGGER.info("用户 {} 邮箱验证成功，缓存已更新", userId);
        } else {
            throw new RuntimeException("邮箱验证失败");
        }
    }

    /**
     * 根据邮箱地址获取用户
     */
    @Override
    public UmsMember getByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        // 使用扩展 Mapper 方法直接根据邮箱查找用户
        return memberExtMapper.selectByEmail(email);
    }

    @Override
    @Transactional
    public void updateMemberInfo(String nickname, String phone) {
        // 获取当前登录用户
        UmsMember currentMember = getCurrentMember();
        if (currentMember == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 验证参数
        if (StrUtil.isEmpty(nickname) && StrUtil.isEmpty(phone)) {
            throw new RuntimeException("昵称和电话号码不能同时为空");
        }
        
        // 如果要更新电话号码,检查是否已被其他用户使用
        if (!StrUtil.isEmpty(phone)) {
            UmsMemberExample example = new UmsMemberExample();
            example.createCriteria()
                .andPhoneEqualTo(phone)
                .andIdNotEqualTo(currentMember.getId());
            List<UmsMember> existingMembers = memberMapper.selectByExample(example);
            if (!CollectionUtils.isEmpty(existingMembers)) {
                throw new RuntimeException("该电话号码已被使用");
            }
        }
        
        // 更新会员信息
        UmsMember updateMember = new UmsMember();
        updateMember.setId(currentMember.getId());
        
        if (!StrUtil.isEmpty(nickname)) {
            updateMember.setNickname(nickname);
        }
        
        if (!StrUtil.isEmpty(phone)) {
            updateMember.setPhone(phone);
        }
        
        int result = memberMapper.updateByPrimaryKeySelective(updateMember);
        if (result <= 0) {
            throw new RuntimeException("更新会员信息失败");
        }
        
        // 清除用户缓存
        memberCacheService.delMember(currentMember.getId());
        
        LOGGER.info("用户 {} 更新会员信息成功", currentMember.getId());
    }
}
