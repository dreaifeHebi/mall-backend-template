package com.macro.mall.service.refund;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.domain.refund.RefundApplyParam;
import com.macro.mall.domain.refund.RefundAuditParam;
import com.macro.mall.domain.refund.RefundRequest;
import com.macro.mall.domain.refund.BatchRefundProcessParam;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 退款服务接口
 * @author dreaifekks
 * @date 2025/10/13
 */
public interface RefundService {

    /**
     * 申请退款（会员端使用）
     * @param param 退款申请参数
     * @param memberId 会员ID
     * @return 退款申请结果
     */
    CommonResult<RefundRequest> applyRefund(RefundApplyParam param, Long memberId);

    /**
     * 管理员代为申请退款
     * @param param 退款申请参数
     * @param memberId 会员ID
     * @param adminId 管理员ID
     * @param adminName 管理员姓名
     * @return 退款申请结果
     */
    CommonResult<RefundRequest> adminApplyRefund(RefundApplyParam param, Long memberId, Long adminId, String adminName);

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
     * @param memberId 会员ID（如果是管理员操作则为null）
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 取消结果
     */
    CommonResult<Void> cancelRefund(Long refundRequestId, Long memberId, Long operatorId, String operatorName);

    /**
     * 获取会员的退款申请列表
     * @param memberId 会员ID
     * @param status 退款状态（可选）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 退款申请列表
     */
    CommonResult<CommonPage<RefundRequest>> getMemberRefundList(Long memberId, String status, Integer pageNum, Integer pageSize);

    /**
     * 获取退款申请详情
     * @param refundRequestId 退款申请ID
     * @param memberId 会员ID（管理员查询时为null）
     * @return 退款申请详情
     */
    CommonResult<RefundRequest> getRefundDetail(Long refundRequestId, Long memberId);

    /**
     * 管理员获取退款申请列表
     * @param status 退款状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param memberUsername 会员用户名（可选）
     * @param orderSn 订单号（可选）
     * @param refundSn 退款单号（可选）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 退款申请列表
     */
    CommonResult<CommonPage<RefundRequest>> getAdminRefundList(String status, Date startDate, Date endDate,
                                                        String memberUsername, String orderSn, String refundSn,
                                                        Integer pageNum, Integer pageSize, Long orderId);

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

    /**
     * 批量处理退款
     * @param param 批量处理参数
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 处理结果
     */
    CommonResult<Map<String, Object>> batchProcessRefunds(BatchRefundProcessParam param, Long operatorId, String operatorName);

    /**
     * 强制退款成功（管理员操作）
     * @param refundRequestId 退款申请ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param reason 强制成功原因
     * @return 处理结果
     */
    CommonResult<RefundRequest> forceRefundSuccess(Long refundRequestId, Long operatorId, String operatorName, String reason);

    /**
     * 强制退款失败（管理员操作）
     * @param refundRequestId 退款申请ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param reason 强制失败原因
     * @return 处理结果
     */
    CommonResult<RefundRequest> forceRefundFail(Long refundRequestId, Long operatorId, String operatorName, String reason);

    /**
     * 重试失败的退款
     * @param refundRequestId 退款申请ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return 处理结果
     */
    CommonResult<RefundRequest> retryFailedRefund(Long refundRequestId, Long operatorId, String operatorName);

    /**
     * 获取退款统计信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计信息
     */
    CommonResult<Map<String, Object>> getRefundStatistics(Date startDate, Date endDate);

    /**
     * 获取每日退款统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日统计数据
     */
    CommonResult<List<Map<String, Object>>> getDailyRefundStatistics(Date startDate, Date endDate);

    /**
     * 处理超时的退款申请
     * @return 处理结果
     */
    CommonResult<Integer> processTimeoutRefunds();
}
