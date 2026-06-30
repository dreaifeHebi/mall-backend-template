package com.macro.mall.portal.service.impl;

import com.macro.mall.common.config.GlobePayConfig;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.dao.PaymentMethodMapper;
import com.macro.mall.portal.dao.PaymentRecordMapper;
import com.macro.mall.portal.config.H5PaymentConfig;
import com.macro.mall.portal.domain.payment.*;
import com.macro.mall.portal.service.H5PaymentService;
import com.macro.mall.portal.service.OmsPortalOrderService;
import com.macro.mall.portal.service.GlobePayService;
import com.macro.mall.portal.service.UmsMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * H5支付服务实现类（集成GlobePay）
 * @author macrozheng
 * @date 2025/7/26
 */
@Service
public class H5PaymentServiceImpl implements H5PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(H5PaymentServiceImpl.class);
    private static final String PROVIDER_LOCAL_SELF = "LOCAL_SELF";
    private static final String CHANNEL_STRIPE_CHECKOUT = "STRIPE_CHECKOUT";

    @Autowired
    private GlobePayConfig globePayConfig;

    @Autowired
    private H5PaymentConfig h5PaymentConfig;

    @Autowired
    private GlobePayService globePayService;

    @Autowired
    private OmsPortalOrderService portalOrderService;

    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private PaymentMethodMapper paymentMethodMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public H5PaymentResponse createPayment(H5PaymentRequest request) {
        H5PaymentResponse response = new H5PaymentResponse();
        
        try {
            // 1. 校验订单归属和支付金额，避免前端传入不可信订单信息
            validatePaymentRequest(request);

            // 2. 保存支付记录
            PaymentRecord record = createPaymentRecord(request);
            paymentRecordMapper.insert(record);

            // 3. 根据Provider处理支付。默认LOCAL_SELF会直接在本系统内确认支付，保留外部支付Provider扩展位。
            String paymentUrl;
            boolean redirectRequired;
            if (isLocalSelfProvider()) {
                paymentUrl = processLocalSelfPayment(request, record);
                redirectRequired = false;
            } else {
                paymentUrl = processPaymentByChannel(request, record);
                redirectRequired = StringUtils.hasText(paymentUrl);
                record.setPaymentUrl(paymentUrl);
                record.setPaymentStatus("PENDING");
                paymentRecordMapper.updateByPrimaryKey(record);
            }

            // 5. 构造响应
            response.setSuccess(true);
            response.setPaymentId(record.getId());
            response.setOrderSn(request.getOrderSn());
            response.setPaymentChannel(request.getPaymentChannel());
            response.setPaymentAmount(request.getTotalAmount());
            response.setPaymentUrl(paymentUrl);
            response.setPaymentStatus(record.getPaymentStatus());
            response.setProvider(currentProvider());
            response.setRedirectRequired(redirectRequired);

            LOGGER.info("创建支付订单成功，订单号: {}, 支付渠道: {}, provider: {}, 状态: {}",
                    request.getOrderSn(), request.getPaymentChannel(), currentProvider(), record.getPaymentStatus());

        } catch (Exception e) {
            LOGGER.error("创建支付订单失败: ", e);
            response.setSuccess(false);
            response.setErrorMessage("创建支付订单失败: " + e.getMessage());
        }

        return response;
    }

    private void validatePaymentRequest(H5PaymentRequest request) {
        if (request == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        PaymentMethod paymentMethod = paymentMethodMapper.selectByMethodCode(request.getPaymentChannel());
        if (paymentMethod == null || paymentMethod.getStatus() == null || paymentMethod.getStatus() != 1) {
            throw new IllegalArgumentException("支付方式不可用");
        }

        UmsMember currentMember = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(request.getOrderId());
        if (order == null || (order.getDeleteStatus() != null && order.getDeleteStatus() == 1)) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!Objects.equals(order.getMemberId(), currentMember.getId())) {
            throw new IllegalArgumentException("无权支付该订单");
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            throw new IllegalArgumentException("当前订单状态不可支付");
        }
        if (!Objects.equals(order.getOrderSn(), request.getOrderSn())) {
            throw new IllegalArgumentException("订单号不匹配");
        }
        if (order.getPayAmount() == null || request.getTotalAmount() == null
                || order.getPayAmount().compareTo(request.getTotalAmount()) != 0) {
            throw new IllegalArgumentException("支付金额不匹配");
        }

        request.setCurrency(StringUtils.hasText(request.getCurrency()) ? request.getCurrency() : "JPY");
    }

    @Override
    public PaymentRecord queryPaymentStatus(String orderSn, String paymentChannel) {
        try {
            PaymentRecord record = paymentRecordMapper.selectByOrderSnAndChannel(orderSn, paymentChannel);
            if (record != null) {
                if (!isLocalSelfProvider()) {
                    // 使用实际的GlobePay API查询订单状态
                    queryPaymentStatusFromGlobePay(record);
                } else {
                    LOGGER.info("本地自确认支付模式，订单状态以本地支付记录为准，订单号: {}", orderSn);
                }
                return record;
            }
        } catch (Exception e) {
            LOGGER.error("查询支付状态失败: ", e);
        }
        return null;
    }

    @Override
    public boolean cancelPayment(String orderSn, String paymentChannel) {
        try {
            PaymentRecord record = paymentRecordMapper.selectByOrderSnAndChannel(orderSn, paymentChannel);
            if (record != null && "PENDING".equals(record.getPaymentStatus())) {
                record.setPaymentStatus("CANCELLED");
                record.setUpdateTime(new Date());
                paymentRecordMapper.updateByPrimaryKey(record);
                
                LOGGER.info("取消支付成功，订单号: {}", orderSn);
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("取消支付失败: ", e);
        }
        return false;
    }

    @Override
    public List<PaymentMethod> getPaymentMethods(String type) {
        try {
            if (StringUtils.hasText(type)) {
                return paymentMethodMapper.selectByType(type);
            } else {
                return paymentMethodMapper.selectEnabledPaymentMethods();
            }
        } catch (Exception e) {
            LOGGER.error("获取支付方式列表失败: ", e);
            return new ArrayList<>();
        }
    }

    @Override
    public String handleAlipayNotify(Map<String, String> params) {
        if (isLocalSelfProvider()) {
            LOGGER.info("本地自确认支付模式，忽略支付宝通知");
            return "success";
        }
        try {
            LOGGER.info("接收到支付宝通知: {}", params);
            
            String orderSn = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");
            
            PaymentRecord record = paymentRecordMapper.selectByOrderSnAndChannel(orderSn, "ALIPAY_H5");
            if (record != null) {
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    record.setPaymentStatus("SUCCESS");
                    record.setPaymentTime(new Date());
                    record.setNotifyTime(new Date());
                    record.setThirdPartyTradeNo(params.get("trade_no"));
                    record.setNotifyResponse(objectMapper.writeValueAsString(params));
                    paymentRecordMapper.updateByPrimaryKey(record);
                    
                    LOGGER.info("支付宝支付成功，订单号: {}", orderSn);
                }
            }
            
            return "success";
        } catch (Exception e) {
            LOGGER.error("处理支付宝通知失败: ", e);
            return "failure";
        }
    }

    @Override
    public String handleWechatNotify(String xmlData) {
        if (isLocalSelfProvider()) {
            LOGGER.info("本地自确认支付模式，忽略微信通知");
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        }
        try {
            LOGGER.info("接收到微信通知: {}", xmlData);
            
            // 这里应该解析微信XML数据，简化处理
            // 模拟处理微信通知
            
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } catch (Exception e) {
            LOGGER.error("处理微信通知失败: ", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>";
        }
    }

    @Override
    public String handleCreditCardNotify(Map<String, String> params) {
        if (isLocalSelfProvider()) {
            LOGGER.info("本地自确认支付模式，忽略信用卡通知");
            return "success";
        }
        try {
            LOGGER.info("接收到信用卡通知: {}", params);
            
            String orderSn = params.get("orderSn");
            String status = params.get("status");
            
            PaymentRecord record = paymentRecordMapper.selectByOrderSnAndChannel(orderSn, "CREDIT_CARD");
            if (record != null) {
                if ("SUCCESS".equals(status)) {
                    record.setPaymentStatus("SUCCESS");
                    record.setPaymentTime(new Date());
                    record.setNotifyTime(new Date());
                    record.setThirdPartyTradeNo(params.get("tradeNo"));
                    record.setNotifyResponse(objectMapper.writeValueAsString(params));
                    paymentRecordMapper.updateByPrimaryKey(record);
                    
                    LOGGER.info("信用卡支付成功，订单号: {}", orderSn);
                }
            }
            
            return "success";
        } catch (Exception e) {
            LOGGER.error("处理信用卡通知失败: ", e);
            return "failure";
        }
    }

    @Override
    public String handleGlobePayNotify(Map<String, String> params) {
        if (isLocalSelfProvider()) {
            LOGGER.info("本地自确认支付模式，忽略GlobePay通知");
            return "success";
        }
        try {
            LOGGER.info("接收到GlobePay通知: {}", params);
            
            // 验证签名
            String partnerCode = params.get("partner_code");
            String timeStr = params.get("time");
            String nonceStr = params.get("nonce_str");
            String sign = params.get("sign");
            
            if (timeStr != null && !timeStr.isEmpty()) {
                long time = Long.parseLong(timeStr);
                if (!globePayService.verifyCallbackSign(partnerCode, time, nonceStr, sign)) {
                    LOGGER.warn("GlobePay回调签名验证失败");
                    return "failure";
                }
            }
            
            String orderSn = params.get("partner_order_id");
            String returnCode = params.get("return_code");
            String resultCode = params.get("result_code");
            
            if (orderSn != null) {
                PaymentRecord record = paymentRecordMapper.selectByOrderSnAndChannel(orderSn, "ALIPAY_H5");
                if (record == null) {
                    record = paymentRecordMapper.selectByOrderSnAndChannel(orderSn, "WECHAT_H5");
                }
                if (record == null) {
                    record = paymentRecordMapper.selectByOrderSnAndChannel(orderSn, "CREDIT_CARD");
                }
                
                if (record != null) {
                    if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
                        record.setPaymentStatus("SUCCESS");
                        record.setPaymentTime(new Date());
                        record.setNotifyTime(new Date());
                        record.setThirdPartyTradeNo(params.get("transaction_id"));
                        record.setNotifyResponse(objectMapper.writeValueAsString(params));
                        paymentRecordMapper.updateByPrimaryKey(record);
                        
                        LOGGER.info("GlobePay支付成功，订单号: {}", orderSn);
                    } else if ("FAIL".equals(returnCode)) {
                        record.setPaymentStatus("FAILED");
                        record.setFailureReason(params.get("return_msg"));
                        record.setNotifyResponse(objectMapper.writeValueAsString(params));
                        paymentRecordMapper.updateByPrimaryKey(record);
                        
                        LOGGER.info("GlobePay支付失败，订单号: {}, 失败原因: {}", orderSn, params.get("return_msg"));
                    }
                }
            }
            
            return "success";
        } catch (Exception e) {
            LOGGER.error("处理GlobePay通知失败: ", e);
            return "failure";
        }
    }

    @Override
    public String handleGlobePayJsonNotify(String jsonData) {
        if (isLocalSelfProvider()) {
            LOGGER.info("本地自确认支付模式，忽略GlobePay JSON通知");
            return "{\"return_code\":\"SUCCESS\"}";
        }
        try {
            LOGGER.info("=== 处理GlobePay JSON格式通知开始 ===");
            LOGGER.info("接收到GlobePay JSON通知: {}", jsonData);
            
            // 解析JSON数据
            @SuppressWarnings("unchecked")
            Map<String, Object> notifyMap = objectMapper.readValue(jsonData, Map.class);
            
            // 验证签名
            String timeStr = String.valueOf(notifyMap.get("time"));
            String nonceStr = String.valueOf(notifyMap.get("nonce_str"));
            String sign = String.valueOf(notifyMap.get("sign"));
            
            if (timeStr != null && !timeStr.isEmpty() && !"null".equals(timeStr)) {
                long time = Long.parseLong(timeStr);
                String partnerCode = globePayConfig.getPartnerCode();
                if (!globePayService.verifyCallbackSign(partnerCode, time, nonceStr, sign)) {
                    LOGGER.warn("GlobePay JSON回调签名验证失败");
                    return "{\"return_code\":\"FAIL\"}";
                }
            }
            
            // 提取关键信息
            String partnerOrderId = String.valueOf(notifyMap.get("partner_order_id"));
            String channelOrderId = String.valueOf(notifyMap.get("channel_order_id"));
            String orderId = String.valueOf(notifyMap.get("order_id"));
            Integer realFee = (Integer) notifyMap.get("real_fee");
            Double rate = (Double) notifyMap.get("rate");
            String currency = String.valueOf(notifyMap.get("currency"));
            String channel = String.valueOf(notifyMap.get("channel"));
            String payTime = String.valueOf(notifyMap.get("pay_time"));
            
            LOGGER.info("=== GlobePay JSON通知解析结果 ===");
            LOGGER.info("商户订单ID: {}", partnerOrderId);
            LOGGER.info("渠道订单ID: {}", channelOrderId);
            LOGGER.info("GlobePay订单ID: {}", orderId);
            LOGGER.info("支付金额: {} {}", realFee, currency);
            LOGGER.info("支付渠道: {}", channel);
            LOGGER.info("支付时间: {}", payTime);
            
            if (partnerOrderId != null && !"null".equals(partnerOrderId)) {
                // 查找支付记录
                PaymentRecord record = paymentRecordMapper.selectByOrderSnAndChannel(partnerOrderId, "ALIPAY_H5");
                if (record == null) {
                    record = paymentRecordMapper.selectByOrderSnAndChannel(partnerOrderId, "WECHAT_H5");
                }
                if (record == null) {
                    record = paymentRecordMapper.selectByOrderSnAndChannel(partnerOrderId, "CREDIT_CARD");
                }
                
                if (record != null) {
                    // 支付成功，更新记录
                    record.setPaymentStatus("SUCCESS");
                    record.setPaymentTime(parseGlobePayTime(payTime));
                    record.setNotifyTime(new Date());
                    record.setThirdPartyOrderId(orderId);
                    record.setThirdPartyTradeNo(channelOrderId);
                    record.setNotifyResponse(jsonData);
                    
                    // 记录汇率信息到响应数据中
                    if (realFee != null && rate != null && !rate.equals(1.0)) {
                        LOGGER.info("支付汇率转换: 1 {} = {} CNY, 支付金额: {} {}", 
                            currency, rate, realFee, currency);
                    }
                    
                    paymentRecordMapper.updateByPrimaryKey(record);
                    
                    LOGGER.info("=== GlobePay JSON支付成功处理完成 ===");
                    LOGGER.info("订单号: {}", partnerOrderId);
                    LOGGER.info("支付状态: SUCCESS");
                    LOGGER.info("第三方订单ID: {}", orderId);
                    LOGGER.info("渠道交易号: {}", channelOrderId);
                    
                    // 处理支付成功后的业务逻辑
                    processPaymentSuccess(record);
                    
                } else {
                    LOGGER.warn("未找到对应的支付记录，订单号: {}", partnerOrderId);
                }
            }
            
            // 返回SUCCESS响应
            return "{\"return_code\":\"SUCCESS\"}";
            
        } catch (Exception e) {
            LOGGER.error("=== 处理GlobePay JSON通知异常 ===");
            LOGGER.error("JSON数据: {}", jsonData);
            LOGGER.error("异常信息: ", e);
            return "{\"return_code\":\"FAIL\"}";
        }
    }

    @Override
    public PaymentRecord confirmPaymentByAdmin(H5PaymentAdminConfirmRequest request) {
        if (request == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        List<PaymentRecord> records = paymentRecordMapper.selectByOrderId(request.getOrderId());
        PaymentRecord record = records.isEmpty() ? null : records.get(0);
        Integer payType = request.getPayType();

        if (payType == null && record != null) {
            payType = getPayTypeByChannel(record.getPaymentChannel());
        }
        if (payType == null) {
            payType = 2;
        }

        portalOrderService.paySuccess(request.getOrderId(), payType);

        if (record != null) {
            if (!"SUCCESS".equals(record.getPaymentStatus())) {
                record.setPaymentStatus("SUCCESS");
                record.setPaymentTime(request.getPaymentTime() != null ? request.getPaymentTime() : new Date());
                record.setNotifyTime(new Date());
                record.setNotifyResponse("ADMIN_CONFIRM");
                record.setUpdateTime(new Date());
                paymentRecordMapper.updateByPrimaryKey(record);
            }
        } else {
            LOGGER.warn("管理员确认支付未找到支付记录，订单ID: {}", request.getOrderId());
        }

        return record;
    }

    /**
     * 创建支付记录
     */
    private PaymentRecord createPaymentRecord(H5PaymentRequest request) {
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(request.getOrderId());
        record.setOrderSn(request.getOrderSn());
        record.setPaymentChannel(request.getPaymentChannel());
        record.setPaymentMethod(getPaymentMethodName(request.getPaymentChannel()));
        record.setPaymentAmount(request.getTotalAmount());
        record.setCurrency(request.getCurrency());
        record.setPaymentStatus("PENDING");
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        
        // 设置过期时间（30分钟后）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        record.setExpireTime(calendar.getTime());
        
        return record;
    }

    private boolean isLocalSelfProvider() {
        return !StringUtils.hasText(currentProvider()) || PROVIDER_LOCAL_SELF.equalsIgnoreCase(currentProvider());
    }

    private String currentProvider() {
        return h5PaymentConfig != null && StringUtils.hasText(h5PaymentConfig.getProvider())
                ? h5PaymentConfig.getProvider().trim().toUpperCase(Locale.ROOT)
                : PROVIDER_LOCAL_SELF;
    }

    private String processLocalSelfPayment(H5PaymentRequest request, PaymentRecord record) throws Exception {
        LOGGER.info("本地自确认支付开始，订单号: {}, 支付渠道: {}", request.getOrderSn(), request.getPaymentChannel());

        Date now = new Date();
        record.setPaymentStatus("SUCCESS");
        record.setPaymentTime(now);
        record.setNotifyTime(now);
        record.setThirdPartyOrderId("LOCAL_SELF_" + record.getId());
        record.setThirdPartyTradeNo("LOCAL_SELF_TXN_" + System.currentTimeMillis());
        record.setPaymentUrl(resolveLocalReturnUrl(request));
        record.setRequestParams(buildLocalSelfPayload(request, "REQUEST"));
        record.setPaymentResponse(buildLocalSelfPayload(request, "SUCCESS"));
        record.setNotifyResponse("LOCAL_SELF_CONFIRM");
        record.setUpdateTime(now);

        portalOrderService.paySuccess(record.getOrderSn(), getPayTypeByChannel(record.getPaymentChannel()));
        paymentRecordMapper.updateByPrimaryKey(record);

        LOGGER.info("本地自确认支付完成，订单号: {}, 支付记录ID: {}", request.getOrderSn(), record.getId());
        return record.getPaymentUrl();
    }

    private String resolveLocalReturnUrl(H5PaymentRequest request) {
        if (h5PaymentConfig != null && StringUtils.hasText(h5PaymentConfig.getLocalReturnUrl())) {
            return h5PaymentConfig.getLocalReturnUrl()
                    .replace("{orderId}", String.valueOf(request.getOrderId()))
                    .replace("{orderSn}", request.getOrderSn())
                    .replace("{paymentChannel}", request.getPaymentChannel());
        }
        return "";
    }

    private String buildLocalSelfPayload(H5PaymentRequest request, String status) throws Exception {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("provider", PROVIDER_LOCAL_SELF);
        payload.put("status", status);
        payload.put("orderId", request.getOrderId());
        payload.put("orderSn", request.getOrderSn());
        payload.put("paymentChannel", request.getPaymentChannel());
        payload.put("amount", request.getTotalAmount());
        payload.put("currency", request.getCurrency());
        payload.put("confirmedAt", new Date());
        return objectMapper.writeValueAsString(payload);
    }

    /**
     * 根据支付渠道处理支付
     */
    private String processPaymentByChannel(H5PaymentRequest request, PaymentRecord record) throws Exception {
        String paymentChannel = request.getPaymentChannel();
        
        switch (paymentChannel) {
            case CHANNEL_STRIPE_CHECKOUT:
                return processStripeCheckoutPayment(request, record);
            case "ALIPAY_H5":
                return processAlipayH5Payment(request, record);
            case "WECHAT_H5":
                return processWechatH5Payment(request, record);
            case "CREDIT_CARD":
                return processCreditCardPayment(request, record);
            default:
                throw new IllegalArgumentException("Unsupported payment channel: " + paymentChannel);
        }
    }

    /**
     * Stripe Checkout Provider占位。默认模板不打包Stripe SDK；生产接入时在这里接入Checkout Session创建。
     */
    private String processStripeCheckoutPayment(H5PaymentRequest request, PaymentRecord record) throws Exception {
        LOGGER.info("处理Stripe Checkout支付，订单号: {}", request.getOrderSn());
        Map<String, Object> stripeRequest = new LinkedHashMap<>();
        stripeRequest.put("provider", "STRIPE");
        stripeRequest.put("mode", "CHECKOUT");
        stripeRequest.put("orderId", request.getOrderId());
        stripeRequest.put("orderSn", request.getOrderSn());
        stripeRequest.put("amount", request.getTotalAmount());
        stripeRequest.put("currency", request.getCurrency());
        stripeRequest.put("successUrl", h5PaymentConfig.getStripe().getSuccessUrl());
        stripeRequest.put("cancelUrl", h5PaymentConfig.getStripe().getCancelUrl());
        record.setRequestParams(objectMapper.writeValueAsString(stripeRequest));

        if (!StringUtils.hasText(h5PaymentConfig.getStripe().getSecretKey())) {
            throw new UnsupportedOperationException("Stripe Checkout provider is not configured; this template uses the LOCAL_SELF provider by default");
        }

        throw new UnsupportedOperationException("Stripe Checkout SDK integration point is reserved; create the Checkout Session in processStripeCheckoutPayment for production");
    }

    /**
     * 处理支付宝H5支付（使用GlobePay）
     */
    private String processAlipayH5Payment(H5PaymentRequest request, PaymentRecord record) throws Exception {
        LOGGER.info("处理支付宝H5支付，订单号: {}", request.getOrderSn());
        
        // 创建GlobePay H5支付请求
        GlobePayH5Request globePayRequest = new GlobePayH5Request();
        globePayRequest.setDescription("日货商城支付-" + request.getOrderSn());
        
        // 将金额转换为最小单位（分）
        BigDecimal amount = request.getTotalAmount();
        if ("CNY".equals(globePayConfig.getDefaultCurrency())) {
            // 人民币：元转分
            globePayRequest.setPrice(amount.multiply(new BigDecimal(100)).intValue());
        } else {
            // 日元：已经是最小单位
            globePayRequest.setPrice(amount.intValue());
        }
        
        globePayRequest.setCurrency(globePayConfig.getDefaultCurrency());
        globePayRequest.setChannel("Alipay");
        globePayRequest.setNotifyUrl(globePayConfig.getNotifyUrl());
        globePayRequest.setOperator("mall-system");
        
        // 保存请求参数
        record.setRequestParams(objectMapper.writeValueAsString(globePayRequest));
        
        // 详细日志记录
        LOGGER.info("=== 支付宝H5支付开始 ===");
        LOGGER.info("订单号: {}, 支付金额: {}, 货币: {}", request.getOrderSn(), request.getTotalAmount(), request.getCurrency());
        
        // 调用GlobePay创建支付订单
        GlobePayH5Response globePayResponse = globePayService.createH5Payment(request.getOrderSn(), globePayRequest);
        
        LOGGER.info("=== 支付宝H5支付结果 ===");
        LOGGER.info("创建状态: {}", globePayResponse.isSuccess() ? "成功" : "失败");
        
        if (globePayResponse.isSuccess()) {
            // 直接使用API返回的pay_url，不再重新生成
            String paymentUrl = globePayResponse.getPayUrl();
            if (paymentUrl == null || paymentUrl.trim().isEmpty()) {
                // 如果API返回的pay_url为空，则抛出异常
                LOGGER.error("API返回的pay_url为空，无法生成支付链接");
                throw new RuntimeException("API返回的支付URL为空");
            }
            
            // 在API返回的payUrl基础上添加redirect等参数
            paymentUrl = globePayService.generateH5PaymentUrl(paymentUrl, globePayConfig.getRedirectUrl() + "?orderSn=" + request.getOrderSn() + "&paymentChannel=ALIPAY_H5");
            
            // 更新支付记录
            record.setThirdPartyOrderId(globePayResponse.getOrderId());
            record.setPaymentResponse(objectMapper.writeValueAsString(globePayResponse));
            
            LOGGER.info("支付宝H5支付订单创建成功，订单号: {}, 支付URL: {}", request.getOrderSn(), paymentUrl);
            return paymentUrl;
        } else {
            LOGGER.error("支付宝H5支付失败: {}", globePayResponse.getReturnMsg());
            throw new RuntimeException("创建支付宝H5支付订单失败: " + globePayResponse.getReturnMsg());
        }
    }

    /**
     * 处理微信H5支付（使用GlobePay）
     */
    private String processWechatH5Payment(H5PaymentRequest request, PaymentRecord record) throws Exception {
        LOGGER.info("处理微信H5支付，订单号: {}", request.getOrderSn());
        
        // 创建GlobePay H5支付请求
        GlobePayH5Request globePayRequest = new GlobePayH5Request();
        globePayRequest.setDescription("日货商城支付-" + request.getOrderSn());
        
        // 将金额转换为最小单位（分）
        BigDecimal amount = request.getTotalAmount();
        if ("CNY".equals(globePayConfig.getDefaultCurrency())) {
            // 人民币：元转分
            globePayRequest.setPrice(amount.multiply(new BigDecimal(100)).intValue());
        } else {
            // 日元：已经是最小单位
            globePayRequest.setPrice(amount.intValue());
        }
        
        globePayRequest.setCurrency(globePayConfig.getDefaultCurrency());
        globePayRequest.setChannel("Wechat");
        globePayRequest.setNotifyUrl(globePayConfig.getNotifyUrl());
        globePayRequest.setOperator("mall-system");
        
        // 保存请求参数
        record.setRequestParams(objectMapper.writeValueAsString(globePayRequest));
        
        // 详细日志记录
        LOGGER.info("=== 微信H5支付开始 ===");
        LOGGER.info("订单号: {}, 支付金额: {}, 货币: {}", request.getOrderSn(), request.getTotalAmount(), request.getCurrency());
        
        // 调用GlobePay创建支付订单
        GlobePayH5Response globePayResponse = globePayService.createH5Payment(request.getOrderSn(), globePayRequest);
        
        LOGGER.info("=== 微信H5支付结果 ===");
        LOGGER.info("创建状态: {}", globePayResponse.isSuccess() ? "成功" : "失败");
        
        if (globePayResponse.isSuccess()) {
            // 直接使用API返回的pay_url，不再重新生成
            String paymentUrl = globePayResponse.getPayUrl();
            if (paymentUrl == null || paymentUrl.trim().isEmpty()) {
                // 如果API返回的pay_url为空，则抛出异常
                LOGGER.error("API返回的pay_url为空，无法生成支付链接");
                throw new RuntimeException("API返回的支付URL为空");
            }
            
            // 在API返回的payUrl基础上添加redirect等参数
            paymentUrl = globePayService.generateH5PaymentUrl(paymentUrl, globePayConfig.getRedirectUrl() + "?orderSn=" + request.getOrderSn() + "&paymentChannel=WECHAT_H5");
            
            // 更新支付记录
            record.setThirdPartyOrderId(globePayResponse.getOrderId());
            record.setPaymentResponse(objectMapper.writeValueAsString(globePayResponse));
            
            LOGGER.info("微信H5支付订单创建成功，订单号: {}, 支付URL: {}", request.getOrderSn(), paymentUrl);
            return paymentUrl;
        } else {
            LOGGER.error("微信H5支付失败: {}", globePayResponse.getReturnMsg());
            throw new RuntimeException("创建微信H5支付订单失败: " + globePayResponse.getReturnMsg());
        }
    }

    /**
     * 处理信用卡支付（使用GlobePay信用卡支付API）
     */
    private String processCreditCardPayment(H5PaymentRequest request, PaymentRecord record) throws Exception {
        LOGGER.info("处理信用卡支付，订单号: {}", request.getOrderSn());
        
        // 创建GlobePay信用卡支付请求
        GlobePayH5Request globePayRequest = new GlobePayH5Request();
        globePayRequest.setDescription("Mall credit card payment-" + request.getOrderSn());
        
        // 将金额转换为最小单位（分）
        BigDecimal amount = request.getTotalAmount();
        if ("CNY".equals(globePayConfig.getDefaultCurrency())) {
            // 人民币：元转分
            globePayRequest.setPrice(amount.multiply(new BigDecimal(100)).intValue());
        } else {
            // 日元：已经是最小单位
            globePayRequest.setPrice(amount.intValue());
        }
        
        globePayRequest.setCurrency(globePayConfig.getDefaultCurrency());
        globePayRequest.setChannel("CREDIT_CARD"); // 使用信用卡支付渠道
        globePayRequest.setNotifyUrl(globePayConfig.getNotifyUrl());
        globePayRequest.setOperator("mall-system");
        
        // 保存请求参数
        record.setRequestParams(objectMapper.writeValueAsString(globePayRequest));
        
        // 详细日志记录 - 模仿支付宝格式
        LOGGER.info("================= 信用卡支付业务开始 =================");
        LOGGER.info("订单号: {}, 支付金额: {}, 货币: {}", request.getOrderSn(), request.getTotalAmount(), request.getCurrency());
        LOGGER.info("请求参数: {}", objectMapper.writeValueAsString(globePayRequest));
        LOGGER.info("=====================================================");
        
        // 调用GlobePay创建信用卡支付订单
        GlobePayH5Response globePayResponse = globePayService.createCreditCardPayment(request.getOrderSn(), globePayRequest);
        
        LOGGER.info("================= 信用卡支付业务响应 =================");
        LOGGER.info("创建状态: {}", globePayResponse.isSuccess() ? "成功" : "失败");
        LOGGER.info("返回码: {}", globePayResponse.getReturnCode());
        LOGGER.info("返回消息: {}", globePayResponse.getReturnMsg());
        LOGGER.info("支付URL: {}", globePayResponse.getPayUrl());
        LOGGER.info("===================================================");
        
        if (globePayResponse.isSuccess()) {
            // 生成信用卡支付跳转URL，使用专门的信用卡成功页面
            String creditCardRedirectUrl = globePayConfig.getCreditCardReturnUrl() != null ? 
                globePayConfig.getCreditCardReturnUrl() : 
                globePayConfig.getRedirectUrl().replace("success.html", "credit-card-success.html");
            String paymentUrl = globePayService.generateCreditCardPaymentUrl(request.getOrderSn(), creditCardRedirectUrl);
            
            // 更新支付记录
            record.setThirdPartyOrderId(globePayResponse.getOrderId());
            record.setPaymentResponse(objectMapper.writeValueAsString(globePayResponse));
            
            LOGGER.info("================= 信用卡支付业务成功 =================");
            LOGGER.info("订单号: {}", request.getOrderSn());
            LOGGER.info("支付URL: {}", paymentUrl);
            LOGGER.info("第三方订单号: {}", globePayResponse.getOrderId());
            LOGGER.info("===================================================");
            return paymentUrl;
        } else {
            LOGGER.error("================= 信用卡支付业务失败 =================");
            LOGGER.error("订单号: {}", request.getOrderSn());
            LOGGER.error("错误码: {}", globePayResponse.getReturnCode());
            LOGGER.error("错误信息: {}", globePayResponse.getReturnMsg());
            LOGGER.error("===================================================");
            
            String errorMsg = String.format("Failed to create credit card payment order - code: %s, message: %s",
                globePayResponse.getReturnCode(), globePayResponse.getReturnMsg());
            throw new RuntimeException(errorMsg);
        }
    }

    /**
     * 使用GlobePay API查询支付状态
     */
    private void queryPaymentStatusFromGlobePay(PaymentRecord record) {
        try {
            // 只查询状态为PENDING的订单
            if (!"PENDING".equals(record.getPaymentStatus())) {
                LOGGER.debug("订单状态非PENDING，无需查询，订单号: {}, 当前状态: {}", record.getOrderSn(), record.getPaymentStatus());
                return;
            }
            
            LOGGER.info("================= 开始查询GlobePay订单状态 =================");
            LOGGER.info("订单号: {}, 支付渠道: {}, 第三方订单ID: {}", record.getOrderSn(), record.getPaymentChannel(), record.getThirdPartyOrderId());
            
            // 使用商户订单号查询（如果有第三方订单ID则优先使用第三方订单ID）
            String queryOrderId = record.getOrderSn();
            
            // 调用GlobePay API查询订单状态
            GlobePayOrderQueryResponse queryResponse = globePayService.queryOrderStatusDetail(queryOrderId);
            
            if (queryResponse.isSuccess()) {
                // 根据GlobePay返回的订单状态更新本地记录
                String globePayStatus = queryResponse.getResultCode();
                boolean statusUpdated = false;
                
                switch (globePayStatus) {
                    case "PAY_SUCCESS":
                        if (!"SUCCESS".equals(record.getPaymentStatus())) {
                            record.setPaymentStatus("SUCCESS");
                            record.setPaymentTime(parseGlobePayTime(queryResponse.getPayTime()));
                            record.setThirdPartyOrderId(queryResponse.getOrderId());
                            record.setThirdPartyTradeNo(queryResponse.getChannelOrderId());
                            record.setUpdateTime(new Date());
                            statusUpdated = true;
                            LOGGER.info("订单支付成功，订单号: {}, GlobePay订单ID: {}, 渠道订单ID: {}", 
                                record.getOrderSn(), queryResponse.getOrderId(), queryResponse.getChannelOrderId());
                            
                            // 处理支付成功后的业务逻辑
                            processPaymentSuccess(record);
                        }
                        break;
                        
                    case "PAY_FAIL":
                    case "CREATE_FAIL":
                        if (!"FAILED".equals(record.getPaymentStatus())) {
                            record.setPaymentStatus("FAILED");
                            record.setFailureReason("GlobePay订单状态: " + globePayStatus);
                            record.setUpdateTime(new Date());
                            statusUpdated = true;
                            LOGGER.info("订单支付失败，订单号: {}, 失败状态: {}", record.getOrderSn(), globePayStatus);
                        }
                        break;
                        
                    case "CLOSED":
                        if (!"CANCELLED".equals(record.getPaymentStatus())) {
                            record.setPaymentStatus("CANCELLED");
                            record.setFailureReason("订单已关闭");
                            record.setUpdateTime(new Date());
                            statusUpdated = true;
                            LOGGER.info("订单已关闭，订单号: {}", record.getOrderSn());
                        }
                        break;
                        
                    case "PAYING":
                        // 仍在支付中，保持PENDING状态
                        LOGGER.info("订单仍在支付中，订单号: {}", record.getOrderSn());
                        break;
                        
                    case "PARTIAL_REFUND":
                    case "FULL_REFUND":
                        // 已退款状态，可以根据需要设置相应状态
                        if (!"REFUNDED".equals(record.getPaymentStatus())) {
                            record.setPaymentStatus("REFUNDED");
                            record.setFailureReason("订单已退款: " + globePayStatus);
                            record.setUpdateTime(new Date());
                            statusUpdated = true;
                            LOGGER.info("订单已退款，订单号: {}, 退款状态: {}", record.getOrderSn(), globePayStatus);
                        }
                        break;
                        
                    default:
                        LOGGER.warn("未知的GlobePay订单状态，订单号: {}, 状态: {}", record.getOrderSn(), globePayStatus);
                        break;
                }
                
                // 如果状态有更新，则保存到数据库
                if (statusUpdated) {
                    // 保存查询响应数据
                    try {
                        record.setPaymentResponse(objectMapper.writeValueAsString(queryResponse));
                    } catch (Exception e) {
                        LOGGER.warn("保存查询响应数据失败，订单号: {}", record.getOrderSn(), e);
                    }
                    
                    paymentRecordMapper.updateByPrimaryKey(record);
                    LOGGER.info("订单状态已更新，订单号: {}, 新状态: {}", record.getOrderSn(), record.getPaymentStatus());
                }
                
            } else {
                LOGGER.warn("查询GlobePay订单状态失败，订单号: {}, 错误信息: {}", record.getOrderSn(), queryResponse.getReturnMsg());
            }
            
            LOGGER.info("================= GlobePay订单状态查询完成 =================");
            
        } catch (Exception e) {
            LOGGER.error("查询GlobePay订单状态异常，订单号: {}", record.getOrderSn(), e);
        }
    }
    
    /**
     * 解析GlobePay时间格式（yyyy-MM-dd HH:mm:ss，GMT+9）
     */
    private Date parseGlobePayTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return new Date();
        }
        
        try {
            // 这里需要根据实际的时间格式进行解析
            // GlobePay返回的是GMT+9时间，可能需要转换为本地时间
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(timeStr);
        } catch (Exception e) {
            LOGGER.warn("解析GlobePay时间失败: {}", timeStr, e);
            return new Date();
        }
    }
    
    /**
     * 模拟查询支付状态（保留作为备用方案）
     */
    @SuppressWarnings("unused")
    private void simulatePaymentStatusQuery(PaymentRecord record) {
        // 模拟支付成功的概率（30%的概率支付成功）
        if ("PENDING".equals(record.getPaymentStatus())) {
            Random random = new Random();
            if (random.nextInt(100) < 30) {
                record.setPaymentStatus("SUCCESS");
                record.setPaymentTime(new Date());
                record.setThirdPartyOrderId("MOCK_" + System.currentTimeMillis());
                record.setThirdPartyTradeNo("TXN_" + System.currentTimeMillis());
                paymentRecordMapper.updateByPrimaryKey(record);
                
                LOGGER.info("模拟支付成功，订单号: {}", record.getOrderSn());
            }
        }
    }

    /**
     * 获取支付方式名称
     */
    private String getPaymentMethodName(String paymentChannel) {
        switch (paymentChannel) {
            case "ALIPAY_H5":
                return "Alipay H5";
            case "WECHAT_H5":
                return "WeChat H5";
            case CHANNEL_STRIPE_CHECKOUT:
                return "Stripe Checkout";
            case "CREDIT_CARD":
                return "Credit Card";
            default:
                return paymentChannel;
        }
    }
    
    /**
     * 处理支付成功后的业务逻辑
     */
    private void processPaymentSuccess(PaymentRecord record) {
        try {
            LOGGER.info("=== 开始处理支付成功业务逻辑 ===");
            LOGGER.info("订单号: {}, 支付渠道: {}, 支付状态: {}", 
                record.getOrderSn(), record.getPaymentChannel(), record.getPaymentStatus());
            
            // 1. 根据订单号查找对应的商城订单
            portalOrderService.paySuccess(record.getOrderSn(), getPayTypeByChannel(record.getPaymentChannel()));
            
            LOGGER.info("=== 支付成功业务逻辑处理完成 ===");
            
        } catch (Exception e) {
            LOGGER.error("处理支付成功业务逻辑失败，订单号: {}", record.getOrderSn(), e);
            // 这里不抛出异常，避免影响通知响应
        }
    }
    
    /**
     * 根据支付渠道获取支付类型
     */
    private Integer getPayTypeByChannel(String paymentChannel) {
        switch (paymentChannel) {
            case "ALIPAY_H5":
                return 1; // 支付宝
            case "WECHAT_H5":
                return 2; // 微信
            case CHANNEL_STRIPE_CHECKOUT:
                return 3; // Stripe/银行卡
            case "CREDIT_CARD":
                return 3; // 信用卡
            default:
                return 0; // 未知
        }
    }
}
