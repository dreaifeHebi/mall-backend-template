package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.SmsEmailTemplateMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.EmailNotificationService;
import com.macro.mall.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Portal邮件通知服务实现类
 * Created by macro on 2025/7/13.
 */
@Slf4j
@Service("portalEmailNotificationService")
public class PortalEmailNotificationServiceImpl implements EmailNotificationService {
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsEmailTemplateMapper emailTemplateMapper;
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    
    @Value("${order.modify.deadline:3}")
    private Integer orderModifyDeadlineDays;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
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
            
            // 获取订单商品列表
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(orderItemExample);
            
            // 格式化商品列表
            StringBuilder orderListBuilder = new StringBuilder();
            if (orderItems != null && !orderItems.isEmpty()) {
                for (OmsOrderItem item : orderItems) {
                    orderListBuilder.append(item.getProductName())
                                   .append(" × ")
                                   .append(item.getProductQuantity())
                                   .append("\n");
                }
            } else {
                orderListBuilder.append("暂无商品信息");
            }
            
            // 替换邮件内容中的变量
            String subject = template.getSubject();
            String content = template.getContent();
            
            subject = subject.replace("[[${userName}]]", member.getUsername());
            subject = subject.replace("[[${orderId}]]", order.getOrderSn());
            
            content = content.replace("[[${userName}]]", member.getUsername());
            content = content.replace("[[${orderId}]]", order.getOrderSn());
            content = content.replace("[[${orderDate}]]", dateFormat.format(order.getCreateTime()));
            content = content.replace("[[${totalAmount}]]", order.getPayAmount().toString());
            content = content.replace("[[${orderList}]]", orderListBuilder.toString());
            
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
            content = content.replace("[[${trackingUrl}]]", trackingUrl);
            
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
            
            // 处理可能为空的地址参数
            String addressInfo = (newAddress != null) ? newAddress : 
                (order.getReceiverProvince() + order.getReceiverCity() + order.getReceiverRegion() + order.getReceiverDetailAddress());
            
            // 计算修改截止时间：订单创建时间 + 配置的修改期限天数
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(order.getCreateTime());
            calendar.add(Calendar.DAY_OF_MONTH, orderModifyDeadlineDays);
            Date modifyDeadline = calendar.getTime();
            String modifyDeadlineStr = dateFormat.format(modifyDeadline);
            
            // 替换邮件内容中的变量
            String subject = template.getSubject();
            String content = template.getContent();
            
            subject = subject.replace("[[${userName}]]", member.getUsername());
            subject = subject.replace("[[${orderId}]]", order.getOrderSn());
            
            content = content.replace("[[${userName}]]", member.getUsername());
            content = content.replace("[[${orderId}]]", order.getOrderSn());
            content = content.replace("[[${nowAddress}]]", addressInfo);
            content = content.replace("[[${modificationTime}]]", dateFormat.format(new java.util.Date()));
            content = content.replace("[[${modifyDeadline}]]", modifyDeadlineStr);
            
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
