package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.payment.GlobePayH5Request;
import com.macro.mall.portal.domain.payment.GlobePayH5Response;
import com.macro.mall.portal.domain.payment.GlobePayOrderQueryResponse;
import com.macro.mall.common.domain.refund.GlobePayRefundRequest;
import com.macro.mall.common.domain.refund.GlobePayRefundResponse;
import com.macro.mall.common.domain.refund.GlobePayRefundQueryResponse;

/**
 * GlobePay支付服务接口
 * @author macrozheng
 * @date 2025/7/26
 */
public interface GlobePayService {
    
    /**
     * 创建H5支付订单
     * @param orderId 商户订单号
     * @param request 支付请求参数
     * @return 支付响应
     */
    GlobePayH5Response createH5Payment(String orderId, GlobePayH5Request request);
    
    /**
     * 生成H5支付跳转URL
     * @param payUrl API返回的支付URL
     * @param redirectUrl 支付成功后跳转页面
     * @return 跳转URL
     */
    String generateH5PaymentUrl(String payUrl, String redirectUrl);
    
    /**
     * 生成Alipay+支付跳转URL
     * @param payUrl API返回的支付URL
     * @param redirectUrl 支付成功后跳转页面
     * @return 跳转URL
     */
    String generateAlipayPlusPaymentUrl(String payUrl, String redirectUrl);
    
    /**
     * 查询订单状态（旧版本，兼容性保留）
     * @param orderId 商户订单号
     * @return 订单状态响应
     */
    GlobePayH5Response queryOrderStatus(String orderId);
    
    /**
     * 查询订单状态详情（推荐使用）
     * @param orderId 商户订单号
     * @return 订单详细状态响应
     */
    GlobePayOrderQueryResponse queryOrderStatusDetail(String orderId);
    
    /**
     * 创建信用卡支付订单 (tokenize模式 - 步骤1: 创建绑卡)
     * @param orderId 商户订单号
     * @param request 支付请求参数
     * @return 支付响应，包含绑卡URL
     */
    GlobePayH5Response createCreditCardPayment(String orderId, GlobePayH5Request request);
    
    /**
     * 查询绑卡结果 (tokenize模式 - 步骤2: 获取member_token)
     * @param requestId 绑卡请求ID
     * @return 绑卡结果，包含member_token
     */
    GlobePayH5Response queryBindCardResult(String requestId);
    
    /**
     * 使用member_token创建支付订单 (tokenize模式 - 步骤3: 完成支付)
     * @param orderId 商户订单号
     * @param memberToken 绑卡获得的token
     * @param request 支付请求参数
     * @return 支付响应
     */
    GlobePayH5Response createTokenizedPayment(String orderId, String memberToken, GlobePayH5Request request);
    
    /**
     * 生成信用卡支付跳转URL
     * @param orderId 商户订单号
     * @param redirectUrl 支付成功后跳转页面
     * @return 跳转URL
     */
    String generateCreditCardPaymentUrl(String orderId, String redirectUrl);
    
    /**
     * 验证回调签名
     * @param partnerCode 商户编码
     * @param time 时间戳
     * @param nonceStr 随机字符串
     * @param sign 签名
     * @return 是否验证通过
     */
    boolean verifyCallbackSign(String partnerCode, long time, String nonceStr, String sign);
    
    /**
     * 申请退款
     * @param request 退款请求参数
     * @return 退款响应
     */
    GlobePayRefundResponse createRefund(GlobePayRefundRequest request);
    
    /**
     * 查询退款状态
     * @param orderId 商户支付订单号
     * @param refundId 商户退款单号
     * @return 退款查询响应
     */
    GlobePayRefundQueryResponse queryRefundStatus(String orderId, String refundId);
}
