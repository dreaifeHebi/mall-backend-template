package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.refund.RefundApplyParam;
import com.macro.mall.common.domain.refund.RefundRequest;

import java.util.List;

/**
 * 会员退款服务接口
 * @author dreaifekks
 * @date 2025/10/14
 */
public interface MemberRefundService {

    /**
     * 申请退款
     */
    CommonResult<RefundRequest> applyRefund(RefundApplyParam param, Long memberId, String memberName);

    /**
     * 取消退款申请
     */
    CommonResult<Void> cancelRefund(Long refundRequestId, Long memberId, String memberName);

    /**
     * 获取会员退款申请列表
     */
    CommonResult<List<RefundRequest>> getMemberRefundList(Long memberId, String status,
                                                         Integer pageNum, Integer pageSize);

    /**
     * 获取会员退款申请详情
     */
    CommonResult<RefundRequest> getMemberRefundDetail(Long refundRequestId, Long memberId);

    /**
     * 查询退款状态
     */
    CommonResult<RefundRequest> queryRefundStatus(Long refundRequestId);
}
