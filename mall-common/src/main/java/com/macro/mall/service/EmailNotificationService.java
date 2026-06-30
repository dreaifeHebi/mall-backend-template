package com.macro.mall.service;

import java.util.Map;

/**
 * 邮件通知服务接口
 * Created by macro on 2025/7/13.
 */
public interface EmailNotificationService {
    
    /**
     * 发送订单确认邮件
     * @param orderId 订单ID
     * @param memberId 用户ID
     */
    void sendOrderConfirmationEmail(Long orderId, Long memberId);
    
    /**
     * 发送国际单号通知邮件
     * @param orderId 订单ID
     * @param memberId 用户ID
     * @param trackingNumber 国际单号
     * @param carrier 承运商
     * @param trackingUrl 跟踪链接
     */
    void sendTrackingNumberEmail(Long orderId, Long memberId, String trackingNumber, String carrier, String trackingUrl);
    
    /**
     * 发送注册确认邮件
     * @param memberId 用户ID
     * @param confirmationLink 邮箱确认链接
     */
    void sendRegistrationConfirmationEmail(Long memberId, String confirmationLink);
    
    /**
     * 发送订单修改通知邮件
     * @param orderId 订单ID
     * @param memberId 用户ID
     * @param newAddress 新的收件地址
     */
    void sendOrderModificationEmail(Long orderId, Long memberId, String newAddress);
    
    /**
     * 发送密码重置邮件
     * @param memberId 用户ID
     * @param resetLink 重置密码链接
     */
    void sendPasswordResetEmail(Long memberId, String resetLink);
    
    /**
     * 根据邮件模板发送邮件
     * @param templateType 模板类型：0-订单确认，1-国际单号通知，2-注册确认，3-订单修改，4-密码重置
     * @param to 收件人邮箱
     * @param variables 模板变量
     */
    void sendEmailByTemplate(Integer templateType, String to, Map<String, Object> variables);
}
