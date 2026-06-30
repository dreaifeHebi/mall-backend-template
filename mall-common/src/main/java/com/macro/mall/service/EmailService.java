package com.macro.mall.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 邮件发送服务
 * Created by mall on 2024/06/22.
 */
@Service
public class EmailService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String smtpUsername;
    
    @Value("${spring.mail.from:mall@example.com}")
    private String fromEmail;
    
    @Value("${mall.email.enable:false}")
    private boolean emailEnabled;
    
    @Value("${mall.email.mode:dev}")
    private String emailMode;

    @Value("${mall.domain}")
    private String domain;
    
    /**
     * 发送简单文本邮件
     */
    public void sendSimpleEmail(String to, String subject, String content) {
        if (!isEmailServiceEnabled()) {
            // 在开发环境中模拟发送
            LOGGER.info("模拟发送邮件到: {} \n主题: {} \n内容: {}", to, subject, content);
            return;
        }
        
        if (mailSender == null) {
            LOGGER.error("邮件发送器未配置，无法发送邮件");
            return;
        }
        
        if (!StringUtils.hasText(fromEmail)) {
            LOGGER.error("发送邮箱地址未配置，无法发送邮件");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            LOGGER.info("邮件发送成功 - 收件人: {}, 主题: {}", to, subject);
        } catch (Exception e) {
            LOGGER.error("邮件发送失败 - 收件人: {}, 主题: {}, 错误: {}", to, subject, e.getMessage());
            throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送HTML邮件（使用简单邮件格式）
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        // 暂时使用简单文本邮件代替HTML邮件
        sendSimpleEmail(to, subject, htmlContent);
    }
    
    /**
     * 发送密码重置邮件
     */
    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "密码重置 - Banana Smoker Mall";
        String content = "您好，\n\n" +
                        "您请求重置密码。请点击以下链接重置您的密码：\n" +
                        domain + "/reset-password?token=" + resetToken + "\n\n" +
                        "此链接将在30分钟后失效。\n" +
                        "如果您没有请求重置密码，请忽略此邮件。\n\n" +
                        "谢谢！\n" +
                        "Banana Smoker Mall 团队";
        
        sendSimpleEmail(to, subject, content);
    }
    
    /**
     * 检查邮件服务是否可用
     */
    public boolean isEmailServiceEnabled() {
        return emailEnabled && "prod".equals(emailMode);
    }
}
