package com.macro.mall.common.service;

import com.macro.mall.common.domain.refund.GlobePayRefundRequest;
import com.macro.mall.common.domain.refund.GlobePayRefundResponse;
import com.macro.mall.common.domain.refund.GlobePayRefundQueryResponse;

/**
 * GlobePay退款服务接口
 * @author dreaifekks
 * @date 2025/10/13
 */
public interface GlobePayRefundService {

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
