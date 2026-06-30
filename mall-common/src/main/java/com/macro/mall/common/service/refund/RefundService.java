package com.macro.mall.common.service.refund;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.refund.RefundApplyParam;
import com.macro.mall.common.domain.refund.RefundAuditParam;
import com.macro.mall.common.domain.refund.RefundRequest;

import java.util.List;

/**
 * 退款服务接口
 * @author macrozheng
 * @date 2025/7/27
 */
public interface RefundService {
    
    /**
     * 申请退款
     * @param param 退款申请参数
     * @param memberId 会员ID
     * @return 退款申请结果
     */
    CommonResult<RefundRequest> applyRefund(RefundApplyParam param, Long memberId);
    
    /**
     * 审核退款申请
     * @param param 审核参数
     * @param auditorId 审核人ID
     * @param auditorName 审核人姓名
     * @return 审核结果
     */
    CommonResult<RefundRequest> auditRefund(RefundAuditParam param, Long auditorId, String auditorName);
    
    /**
     * 处理退款（发起第三方退款）
     * @param refundRequestId 退款申请ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 处理结果
     */
    CommonResult<RefundRequest> processRefund(Long refundRequestId, Long operatorId, String operatorName);
    
    /**
     * 查询退款状态
     * @param refundRequestId 退款申请ID
     * @return 查询结果
     */
    CommonResult<RefundRequest> queryRefundStatus(Long refundRequestId);
    
    /**
     * 取消退款申请
     * @param refundRequestId 退款申请ID
     * @param memberId 会员ID
     * @return 取消结果
     */
    CommonResult<Void> cancelRefund(Long refundRequestId, Long memberId);
    
    /**
     * 获取会员的退款申请列表
     * @param memberId 会员ID
     * @param status 退款状态（可选）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 退款申请列表
     */
    CommonResult<List<RefundRequest>> getMemberRefundList(Long memberId, String status, Integer pageNum, Integer pageSize);
    
    /**
     * 获取退款申请详情
     * @param refundRequestId 退款申请ID
     * @param memberId 会员ID
     * @return 退款申请详情
     */
    CommonResult<RefundRequest> getRefundDetail(Long refundRequestId, Long memberId);
    
    /**
     * 管理员获取退款申请列表
     * @param status 退款状态（可选）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 退款申请列表
     */
    CommonResult<List<RefundRequest>> getAdminRefundList(String status, Integer pageNum, Integer pageSize);
    
    /**
     * 管理员获取退款申请详情
     * @param refundRequestId 退款申请ID
     * @return 退款申请详情
     */
    CommonResult<RefundRequest> getAdminRefundDetail(Long refundRequestId);
    
    /**
     * 系统自动查询待处理的退款状态
     * @return 处理结果
     */
    CommonResult<Integer> autoQueryPendingRefunds();
}
