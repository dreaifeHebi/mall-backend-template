package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.payment.H5PaymentRequest;
import com.macro.mall.portal.domain.payment.H5PaymentResponse;
import com.macro.mall.portal.domain.payment.H5PaymentAdminConfirmRequest;
import com.macro.mall.portal.domain.payment.PaymentMethod;
import com.macro.mall.portal.domain.payment.PaymentRecord;

import java.util.List;
import java.util.Map;

/**
 * H5支付服务接口
 * @author macrozheng
 * @date 2025/7/26
 */
public interface H5PaymentService {

    /**
     * 创建H5支付订单
     */
    H5PaymentResponse createPayment(H5PaymentRequest request);

    /**
     * 查询支付状态
     */
    PaymentRecord queryPaymentStatus(String orderSn, String paymentChannel);

    /**
     * 取消支付
     */
    boolean cancelPayment(String orderSn, String paymentChannel);

    /**
     * 获取支付方式列表
     */
    List<PaymentMethod> getPaymentMethods(String type);

    /**
     * 支付宝通知处理
     */
    String handleAlipayNotify(Map<String, String> params);

    /**
     * 微信通知处理
     */
    String handleWechatNotify(String xmlData);

    /**
     * 信用卡通知处理
     */
    String handleCreditCardNotify(Map<String, String> params);
    
    /**
     * GlobePay通知处理（表单格式）
     */
    String handleGlobePayNotify(Map<String, String> params);
    
    /**
     * GlobePay通知处理（JSON格式）
     */
    String handleGlobePayJsonNotify(String jsonData);

    /**
     * 管理员确认支付
     */
    PaymentRecord confirmPaymentByAdmin(H5PaymentAdminConfirmRequest request);
}
