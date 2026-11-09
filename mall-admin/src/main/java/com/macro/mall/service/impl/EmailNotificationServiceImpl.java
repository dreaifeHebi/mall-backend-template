package com.macro.mall.service.impl;

import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.SmsEmailTemplateMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.SmsEmailTemplate;
import com.macro.mall.model.UmsMember;
import com.macro.mall.service.EmailNotificationService;
import com.macro.mall.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * 邮件通知服务实现类
 * Created by macro on 2025/7/13.
 */
@Slf4j
@Service("adminEmailNotificationService")
public class EmailNotificationServiceImpl implements EmailNotificationService {
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsEmailTemplateMapper emailTemplateMapper;
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Value("${frontend.base-url}")
    private String frontendBaseUrl;
    
    @Value("${frontend.order-detail-path}")
    private String orderDetailPath;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 生成订单详情页面URL
     * @param orderId 订单ID
     * @return 订单详情页面URL
     */
    private String generateOrderDetailUrl(Long orderId) {
        return frontendBaseUrl + orderDetailPath + "?orderId=" + orderId;
    }
    
    @Override
    public void sendOrderConfirmationEmail(Long orderId, Long memberId) {
        try {
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            
            if (order == null || member == null || !StringUtils.hasText(member.getEmail())) {
                log.warn("订单确认邮件发送失败，订单或用户信息不存在或邮箱为空");
                return;
            }
            
            // 获取邮件模板
            List<SmsEmailTemplate> templates = emailTemplateMapper.selectByTriggerSceneAndStatus(0, 1);
            if (templates == null || templates.isEmpty()) {
                log.warn("未找到订单确认邮件模板");
                return;
            }
            
            SmsEmailTemplate template = templates.get(0);
            
            // 替换邮件内容中的变量
            String subject = template.getSubject();
            String content = template.getContent();
            
            subject = subject.replace("[[${userName}]]", member.getUsername());
            subject = subject.replace("[[${orderId}]]", order.getOrderSn());
            
            content = content.replace("[[${userName}]]", member.getUsername());
            content = content.replace("[[${orderId}]]", order.getOrderSn());
            content = content.replace("[[${orderDate}]]", dateFormat.format(order.getCreateTime()));
            content = content.replace("[[${totalAmount}]]", order.getPayAmount().toString());
            content = content.replace("[[${orderList}]]", "商品详情请查看订单详情页面");
            
            emailService.sendSimpleEmail(member.getEmail(), subject, content);
            log.info("订单确认邮件发送成功，订单号：{}，收件人：{}", order.getOrderSn(), member.getEmail());
            
        } catch (Exception e) {
            log.error("发送订单确认邮件失败", e);
        }
    }
    
    @Override
    public void sendTrackingNumberEmail(Long orderId, Long memberId, String trackingNumber, String carrier, String trackingUrl) {
        try {
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            
            if (order == null || member == null || !StringUtils.hasText(member.getEmail())) {
                log.warn("国际单号通知邮件发送失败，订单或用户信息不存在或邮箱为空");
                return;
            }
            
            // 获取邮件模板
            List<SmsEmailTemplate> templates = emailTemplateMapper.selectByTriggerSceneAndStatus(1, 1);
            if (templates == null || templates.isEmpty()) {
                log.warn("未找到国际单号通知邮件模板");
                return;
            }
            
            SmsEmailTemplate template = templates.get(0);
            
            // 替换邮件内容中的变量
            String subject = template.getSubject();
            String content = template.getContent();
            
            subject = subject.replace("[[${userName}]]", member.getUsername());
            subject = subject.replace("[[${orderId}]]", order.getOrderSn());
            
            content = content.replace("[[${userName}]]", member.getUsername());
            content = content.replace("[[${orderId}]]", order.getOrderSn());
            content = content.replace("[[${trackingNumber}]]", trackingNumber);
            content = content.replace("[[${carrier}]]", carrier);
            
            // 生成订单详情页面URL用于trackingUrl变量
            String orderDetailUrl = generateOrderDetailUrl(orderId);
            content = content.replace("[[${trackingUrl}]]", orderDetailUrl);
            
            emailService.sendSimpleEmail(member.getEmail(), subject, content);
            log.info("国际单号通知邮件发送成功，订单号：{}，收件人：{}", order.getOrderSn(), member.getEmail());
            
        } catch (Exception e) {
            log.error("发送国际单号通知邮件失败", e);
        }
    }
    
    @Override
    public void sendRegistrationConfirmationEmail(Long memberId, String confirmationLink) {
        try {
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            
            if (member == null || !StringUtils.hasText(member.getEmail())) {
                log.warn("注册确认邮件发送失败，用户信息不存在或邮箱为空");
                return;
            }
            
            // 获取邮件模板
            List<SmsEmailTemplate> templates = emailTemplateMapper.selectByTriggerSceneAndStatus(2, 1);
            if (templates == null || templates.isEmpty()) {
                log.warn("未找到注册确认邮件模板");
                return;
            }
            
            SmsEmailTemplate template = templates.get(0);
            
            // 替换邮件内容中的变量
            String subject = template.getSubject();
            String content = template.getContent();
            
            subject = subject.replace("[[${userName}]]", member.getUsername());
            
            content = content.replace("[[${userName}]]", member.getUsername());
            content = content.replace("[[${confirmationLink}]]", confirmationLink);
            
            emailService.sendSimpleEmail(member.getEmail(), subject, content);
            log.info("注册确认邮件发送成功，用户：{}，收件人：{}", member.getUsername(), member.getEmail());
            
        } catch (Exception e) {
            log.error("发送注册确认邮件失败", e);
        }
    }
    
    @Override
    public void sendOrderModificationEmail(Long orderId, Long memberId, String newAddress) {
        try {
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            
            if (order == null || member == null || !StringUtils.hasText(member.getEmail())) {
                log.warn("订单修改通知邮件发送失败，订单或用户信息不存在或邮箱为空");
                return;
            }
            
            // 获取邮件模板
            List<SmsEmailTemplate> templates = emailTemplateMapper.selectByTriggerSceneAndStatus(3, 1);
            if (templates == null || templates.isEmpty()) {
                log.warn("未找到订单修改通知邮件模板");
                return;
            }
            
            SmsEmailTemplate template = templates.get(0);
            
            // 替换邮件内容中的变量
            String subject = template.getSubject();
            String content = template.getContent();
            
            subject = subject.replace("[[${userName}]]", member.getUsername());
            subject = subject.replace("[[${orderId}]]", order.getOrderSn());
            
            content = content.replace("[[${userName}]]", member.getUsername());
            content = content.replace("[[${orderId}]]", order.getOrderSn());
            content = content.replace("[[${nowAddress}]]", newAddress);
            content = content.replace("[[${modificationTime}]]", dateFormat.format(new java.util.Date()));
            
            emailService.sendSimpleEmail(member.getEmail(), subject, content);
            log.info("订单修改通知邮件发送成功，订单号：{}，收件人：{}", order.getOrderSn(), member.getEmail());
            
        } catch (Exception e) {
            log.error("发送订单修改通知邮件失败", e);
        }
    }
    
    @Override
    public void sendPasswordResetEmail(Long memberId, String resetLink) {
        try {
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            
            if (member == null || !StringUtils.hasText(member.getEmail())) {
                log.warn("密码重置邮件发送失败，用户信息不存在或邮箱为空");
                return;
            }
            
            // 获取邮件模板
            List<SmsEmailTemplate> templates = emailTemplateMapper.selectByTriggerSceneAndStatus(4, 1);
            if (templates == null || templates.isEmpty()) {
                log.warn("未找到密码重置邮件模板");
                return;
            }
            
            SmsEmailTemplate template = templates.get(0);
            
            // 替换邮件内容中的变量
            String subject = template.getSubject();
            String content = template.getContent();
            
            subject = subject.replace("[[${userName}]]", member.getUsername());
            
            content = content.replace("[[${userName}]]", member.getUsername());
            content = content.replace("[[${resetLink}]]", resetLink);
            
            emailService.sendSimpleEmail(member.getEmail(), subject, content);
            log.info("密码重置邮件发送成功，用户：{}，收件人：{}", member.getUsername(), member.getEmail());
            
        } catch (Exception e) {
            log.error("发送密码重置邮件失败", e);
        }
    }
    
    @Override
    public void sendEmailByTemplate(Integer templateType, String to, Map<String, Object> variables) {
        try {
            if (!StringUtils.hasText(to)) {
                log.warn("邮件发送失败，收件人邮箱为空");
                return;
            }
            
            // 获取邮件模板
            List<SmsEmailTemplate> templates = emailTemplateMapper.selectByTriggerSceneAndStatus(templateType, 1);
            if (templates == null || templates.isEmpty()) {
                log.warn("未找到邮件模板，模板类型：{}", templateType);
                return;
            }
            
            SmsEmailTemplate template = templates.get(0);
            
            // 替换邮件内容中的变量
            String subject = template.getSubject();
            String content = template.getContent();
            
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "[[${" + entry.getKey() + "}]]";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                
                subject = subject.replace(placeholder, value);
                content = content.replace(placeholder, value);
            }
            
            emailService.sendSimpleEmail(to, subject, content);
            log.info("邮件发送成功，模板类型：{}，收件人：{}", templateType, to);
            
        } catch (Exception e) {
            log.error("发送邮件失败", e);
        }
    }
}
