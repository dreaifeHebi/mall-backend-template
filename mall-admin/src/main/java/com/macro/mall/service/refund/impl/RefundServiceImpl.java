package com.macro.mall.service.refund.impl;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dao.refund.RefundRequestDao;
import com.macro.mall.domain.refund.*;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.UmsMember;
import com.macro.mall.service.refund.RefundLogService;
import com.macro.mall.service.refund.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 退款服务实现类
 *
 * @author dreaifekks
 * @date 2025/10/13
 */
@Service
public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefundServiceImpl.class);

    private static final int TIMEOUT_HOURS = 24;
    private static final int BATCH_SIZE = 100;

    // 异步处理线程池

    @Autowired
    private RefundRequestDao refundRequestDao;

    // @Autowired
    // private RefundLogService refundLogService;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Override
    @Transactional
    public CommonResult<RefundRequest> applyRefund(RefundApplyParam param, Long memberId) {
        return adminApplyRefund(param, memberId, null, null);
    }

    @Override
    @Transactional
    public CommonResult<RefundRequest> adminApplyRefund(RefundApplyParam param, Long memberId, Long adminId,
            String adminName) {
        try {
            // 验证订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(param.getOrderId());
            if (order == null) {
                return CommonResult.failed("订单不存在");
            }

            // 验证会员信息
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member == null) {
                return CommonResult.failed("会员不存在");
            }

            // 验证退款金额
            if (param.getRefundAmount().compareTo(order.getPayAmount()) > 0) {
                return CommonResult.failed("退款金额不能超过订单金额");
            }

            // 检查是否已有退款申请
            List<RefundRequest> existingRefunds = refundRequestDao.selectByOrderId(param.getOrderId());
            BigDecimal totalRefunded = existingRefunds.stream()
                    .filter(r -> !RefundStatus.REJECTED.getCode().equals(r.getStatus()) &&
                            !RefundStatus.CANCELLED.getCode().equals(r.getStatus()))
                    .map(RefundRequest::getRefundAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalRefunded.add(param.getRefundAmount()).compareTo(order.getPayAmount()) > 0) {
                return CommonResult.failed("累计退款金额不能超过订单金额");
            }

            // 生成退款单号
            String refundSn = generateRefundSn();

            // 创建退款申请
            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setRefundSn(refundSn);
            refundRequest.setOrderId(param.getOrderId());
            refundRequest.setOrderSn(order.getOrderSn());
            refundRequest.setPaymentRecordId(order.getId()); // 这里简化处理，实际应该有专门的支付记录表
            refundRequest.setMemberId(memberId);
            refundRequest.setMemberUsername(member.getUsername());
            refundRequest.setRefundAmount(param.getRefundAmount());
            refundRequest.setRefundReason(param.getRefundReason());
            refundRequest.setStatus(RefundStatus.PENDING_AUDIT.getCode());
            refundRequest.setApplyTime(new Date());
            refundRequest.setCreateTime(new Date());
            refundRequest.setUpdateTime(new Date());

            // 插入数据库
            int result = refundRequestDao.insert(refundRequest);
            if (result <= 0) {
                return CommonResult.failed("申请退款失败");
            }

            // 记录日志
            String operatorName = adminName != null ? adminName : member.getUsername();
            Long operatorId = adminId != null ? adminId : memberId;

            // refundLogService.recordLog(
            // refundRequest.getId(),
            // refundSn,
            // RefundOperationType.APPLY,
            // adminId != null ? "管理员代为申请退款: " + param.getRefundReason() : "用户申请退款: " +
            // param.getRefundReason(),
            // "SUCCESS",
            // objectMapper.writeValueAsString(param),
            // null,
            // null,
            // operatorId,
            // operatorName
            // );

            return CommonResult.success(refundRequest, "退款申请提交成功，请等待审核");

        } catch (Exception e) {
            LOGGER.error("申请退款异常", e);
            return CommonResult.failed("申请退款异常: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CommonResult<RefundRequest> auditRefund(RefundAuditParam param, Long auditorId, String auditorName) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(param.getRefundRequestId());
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }

            RefundStatus currentStatus = RefundStatus.fromCode(refundRequest.getStatus());
            if (!currentStatus.canAudit()) {
                return CommonResult.failed("当前状态不允许审核");
            }

            String auditStatus = param.getAuditStatus();
            RefundStatus newStatus = RefundStatus.fromCode(auditStatus);

            // 更新审核信息
            refundRequestDao.updateAuditInfo(
                    param.getRefundRequestId(),
                    auditStatus,
                    auditorId,
                    auditorName,
                    param.getAuditNote());

            // 记录审核日志
            RefundOperationType operationType = newStatus == RefundStatus.APPROVED ? RefundOperationType.AUDIT_APPROVED
                    : RefundOperationType.AUDIT_REJECTED;

            // refundLogService.recordLog(
            // refundRequest.getId(),
            // refundRequest.getRefundSn(),
            // operationType,
            // "审核退款申请: " + (param.getAuditNote() != null ? param.getAuditNote() : ""),
            // "SUCCESS",
            // objectMapper.writeValueAsString(param),
            // null,
            // null,
            // auditorId,
            // auditorName
            // );

            // 如果审核通过且要求立即处理，则发起退款
            if (newStatus == RefundStatus.APPROVED && Boolean.TRUE.equals(param.getImmediateProcess())) {
                return processRefund(param.getRefundRequestId(), auditorId, auditorName);
            }

            // 重新查询返回
            refundRequest = refundRequestDao.selectById(param.getRefundRequestId());
            return CommonResult.success(refundRequest, "审核完成");

        } catch (Exception e) {
            LOGGER.error("审核退款异常", e);
            return CommonResult.failed("审核退款异常: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CommonResult<RefundRequest> processRefund(Long refundRequestId, Long operatorId, String operatorName) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(refundRequestId);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }

            RefundStatus currentStatus = RefundStatus.fromCode(refundRequest.getStatus());
            if (currentStatus != RefundStatus.APPROVED && currentStatus != RefundStatus.FAILED) {
                return CommonResult.failed("当前状态不允许处理退款");
            }
            refundRequestDao.updateStatus(refundRequestId, RefundStatus.SUCCESS.getCode(), null, null);
            updateOrderStatusOnRefundSuccess(refundRequest);
            refundRequest = refundRequestDao.selectById(refundRequestId);
            return CommonResult.success(refundRequest, "退款处理成功");

        } catch (Exception e) {
            LOGGER.error("处理退款异常", e);
            return CommonResult.failed("处理退款异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<RefundRequest> queryRefundStatus(Long refundRequestId) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(refundRequestId);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }
            return CommonResult.success(refundRequest);

        } catch (Exception e) {
            LOGGER.error("查询退款状态异常", e);
            return CommonResult.failed("查询退款状态异常: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CommonResult<Void> cancelRefund(Long refundRequestId, Long memberId, Long operatorId, String operatorName) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(refundRequestId);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }

            // 权限检查
            if (memberId != null && !refundRequest.getMemberId().equals(memberId)) {
                return CommonResult.failed("无权操作此退款申请");
            }

            RefundStatus currentStatus = RefundStatus.fromCode(refundRequest.getStatus());
            if (!currentStatus.canCancel()) {
                return CommonResult.failed("当前状态不允许取消");
            }

            // 更新状态为已取消
            refundRequestDao.updateStatus(refundRequestId, RefundStatus.CANCELLED.getCode(), null, null);

            // 记录取消日志
            // refundLogService.recordLog(
            // refundRequest.getId(),
            // refundRequest.getRefundSn(),
            // RefundOperationType.CANCEL,
            // memberId != null ? "用户取消退款申请" : "管理员取消退款申请",
            // "SUCCESS",
            // null,
            // null,
            // null,
            // operatorId,
            // operatorName
            // );

            return CommonResult.success(null, "退款申请已取消");

        } catch (Exception e) {
            LOGGER.error("取消退款异常", e);
            return CommonResult.failed("取消退款异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<CommonPage<RefundRequest>> getMemberRefundList(Long memberId, String status, Integer pageNum,
            Integer pageSize) {
        try {
            int safePageNum = normalizePageNum(pageNum);
            int safePageSize = normalizePageSize(pageSize);
            int offset = (safePageNum - 1) * safePageSize;
            List<RefundRequest> refundList = refundRequestDao.selectByMemberId(memberId, status, offset, safePageSize);
            int total = refundRequestDao.countByMemberId(memberId, status);
            return CommonResult.success(buildPage(refundList, safePageNum, safePageSize, total));
        } catch (Exception e) {
            LOGGER.error("获取会员退款列表异常", e);
            return CommonResult.failed("获取退款列表异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<RefundRequest> getRefundDetail(Long refundRequestId, Long memberId) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(refundRequestId);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }

            // 权限检查
            if (memberId != null && !refundRequest.getMemberId().equals(memberId)) {
                return CommonResult.failed("无权查看此退款申请");
            }

            return CommonResult.success(refundRequest);
        } catch (Exception e) {
            LOGGER.error("获取退款详情异常", e);
            return CommonResult.failed("获取退款详情异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<CommonPage<RefundRequest>> getAdminRefundList(String status, Date startDate, Date endDate,
            String memberUsername, String orderSn, String refundSn,
            Integer pageNum, Integer pageSize, Long orderId) {
        try {
            int safePageNum = normalizePageNum(pageNum);
            int safePageSize = normalizePageSize(pageSize);
            int offset = (safePageNum - 1) * safePageSize;
            List<RefundRequest> refundList = refundRequestDao.selectForAdmin(
                    status, startDate, endDate, memberUsername, orderSn, refundSn, offset, safePageSize, orderId);
            int total = refundRequestDao.countForAdmin(
                    status, startDate, endDate, memberUsername, orderSn, refundSn, orderId);
            return CommonResult.success(buildPage(refundList, safePageNum, safePageSize, total));
        } catch (Exception e) {
            LOGGER.error("获取管理员退款列表异常", e);
            return CommonResult.failed("获取退款列表异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<RefundRequest> getAdminRefundDetail(Long refundRequestId) {
        return getRefundDetail(refundRequestId, null);
    }

    @Override
    public CommonResult<Integer> autoQueryPendingRefunds() {
        try {
            return CommonResult.success(0, "第三方退款已停用，无需自动查询");

        } catch (Exception e) {
            LOGGER.error("自动查询待处理退款异常", e);
            return CommonResult.failed("自动查询异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Map<String, Object>> batchProcessRefunds(BatchRefundProcessParam param, Long operatorId,
            String operatorName) {
        try {
            List<Long> refundRequestIds = param.getRefundRequestIds();
            String operationType = param.getOperationType();
            Map<String, Object> result = new HashMap<>();
            List<Long> successIds = new ArrayList<>();
            List<Map<String, Object>> failedResults = new ArrayList<>();

            if (CollectionUtils.isEmpty(refundRequestIds)) {
                return CommonResult.failed("退款申请ID列表不能为空");
            }

            // 查询所有退款申请
            List<RefundRequest> refundRequests = refundRequestDao.selectByIds(refundRequestIds);
            if (refundRequests.size() != refundRequestIds.size()) {
                return CommonResult.failed("部分退款申请不存在");
            }

            // 根据操作类型处理
            for (RefundRequest refundRequest : refundRequests) {
                try {
                    switch (operationType) {
                        case "AUDIT_APPROVED":
                            RefundAuditParam auditParam = new RefundAuditParam();
                            auditParam.setRefundRequestId(refundRequest.getId());
                            auditParam.setAuditStatus(RefundStatus.APPROVED.getCode());
                            auditParam.setAuditNote(param.getOperationNote());
                            CommonResult<RefundRequest> auditResult = auditRefund(auditParam, operatorId, operatorName);
                            if (auditResult.getCode() == 200) {
                                successIds.add(refundRequest.getId());
                            } else {
                                Map<String, Object> failedItem = new HashMap<>();
                                failedItem.put("refundRequestId", refundRequest.getId());
                                failedItem.put("reason", auditResult.getMessage());
                                failedResults.add(failedItem);
                            }
                            break;
                        case "AUDIT_REJECTED":
                            RefundAuditParam rejectParam = new RefundAuditParam();
                            rejectParam.setRefundRequestId(refundRequest.getId());
                            rejectParam.setAuditStatus(RefundStatus.REJECTED.getCode());
                            rejectParam.setAuditNote(param.getOperationNote());
                            CommonResult<RefundRequest> rejectResult = auditRefund(rejectParam, operatorId,
                                    operatorName);
                            if (rejectResult.getCode() == 200) {
                                successIds.add(refundRequest.getId());
                            } else {
                                Map<String, Object> failedItem = new HashMap<>();
                                failedItem.put("refundRequestId", refundRequest.getId());
                                failedItem.put("reason", rejectResult.getMessage());
                                failedResults.add(failedItem);
                            }
                            break;
                        case "PROCESS":
                            CommonResult<RefundRequest> processResult = processRefund(refundRequest.getId(), operatorId,
                                    operatorName);
                            if (processResult.getCode() == 200) {
                                successIds.add(refundRequest.getId());
                            } else {
                                Map<String, Object> failedItem = new HashMap<>();
                                failedItem.put("refundRequestId", refundRequest.getId());
                                failedItem.put("reason", processResult.getMessage());
                                failedResults.add(failedItem);
                            }
                            break;
                        case "QUERY":
                            CommonResult<RefundRequest> queryResult = queryRefundStatus(refundRequest.getId());
                            if (queryResult.getCode() == 200) {
                                successIds.add(refundRequest.getId());
                            } else {
                                Map<String, Object> failedItem = new HashMap<>();
                                failedItem.put("refundRequestId", refundRequest.getId());
                                failedItem.put("reason", queryResult.getMessage());
                                failedResults.add(failedItem);
                            }
                            break;
                        default:
                            Map<String, Object> failedItem = new HashMap<>();
                            failedItem.put("refundRequestId", refundRequest.getId());
                            failedItem.put("reason", "不支持的操作类型: " + operationType);
                            failedResults.add(failedItem);
                    }
                } catch (Exception e) {
                    LOGGER.error("批量处理退款异常: refundId={}", refundRequest.getId(), e);
                    Map<String, Object> failedItem = new HashMap<>();
                    failedItem.put("refundRequestId", refundRequest.getId());
                    failedItem.put("reason", e.getMessage());
                    failedResults.add(failedItem);
                }
            }

            // 记录批量操作日志
            // refundLogService.recordLog(
            // null,
            // null,
            // RefundOperationType.BATCH_PROCESS,
            // "批量" + operationType + "，成功: " + successIds.size() + "，失败: " +
            // failedResults.size(),
            // "SUCCESS",
            // objectMapper.writeValueAsString(param),
            // objectMapper.writeValueAsString(result),
            // null,
            // operatorId,
            // operatorName
            // );

            result.put("totalCount", refundRequestIds.size());
            result.put("successCount", successIds.size());
            result.put("failedCount", failedResults.size());
            result.put("successIds", successIds);
            result.put("failedResults", failedResults);

            return CommonResult.success(result, "批量处理完成");

        } catch (Exception e) {
            LOGGER.error("批量处理退款异常", e);
            return CommonResult.failed("批量处理异常: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CommonResult<RefundRequest> forceRefundSuccess(Long refundRequestId, Long operatorId, String operatorName,
            String reason) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(refundRequestId);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }

            // 更新状态为成功
            refundRequestDao.updateStatus(refundRequestId, RefundStatus.SUCCESS.getCode(), null, null);

            // 更新订单状态
            updateOrderStatusOnRefundSuccess(refundRequest);

            // 记录强制成功日志
            // refundLogService.recordLog(
            // refundRequest.getId(),
            // refundRequest.getRefundSn(),
            // RefundOperationType.ADMIN_FORCE_COMPLETE,
            // "管理员强制退款成功: " + reason,
            // "SUCCESS",
            // reason,
            // null,
            // null,
            // operatorId,
            // operatorName
            // );

            // 重新查询返回
            refundRequest = refundRequestDao.selectById(refundRequestId);
            return CommonResult.success(refundRequest, "强制退款成功");

        } catch (Exception e) {
            LOGGER.error("强制退款成功异常", e);
            return CommonResult.failed("强制退款成功异常: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CommonResult<RefundRequest> forceRefundFail(Long refundRequestId, Long operatorId, String operatorName,
            String reason) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(refundRequestId);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }

            // 更新状态为失败
            refundRequestDao.updateStatus(refundRequestId, RefundStatus.FAILED.getCode(), null, reason);

            // 记录强制失败日志
            // refundLogService.recordLog(
            // refundRequest.getId(),
            // refundRequest.getRefundSn(),
            // RefundOperationType.ADMIN_FORCE_FAIL,
            // "管理员强制退款失败: " + reason,
            // "SUCCESS",
            // reason,
            // null,
            // null,
            // operatorId,
            // operatorName
            // );

            // 重新查询返回
            refundRequest = refundRequestDao.selectById(refundRequestId);
            return CommonResult.success(refundRequest, "强制退款失败");

        } catch (Exception e) {
            LOGGER.error("强制退款失败异常", e);
            return CommonResult.failed("强制退款失败异常: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CommonResult<RefundRequest> retryFailedRefund(Long refundRequestId, Long operatorId, String operatorName) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(refundRequestId);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }

            RefundStatus currentStatus = RefundStatus.fromCode(refundRequest.getStatus());
            if (!currentStatus.canReprocess()) {
                return CommonResult.failed("当前状态不允许重试");
            }

            // 检查是否已达到最大重试次数（由于表结构简化，暂时跳过此检查）
            // if (refundRequest.getProcessCount() >= MAX_RETRY_COUNT) {
            // return CommonResult.failed("已达到最大重试次数");
            // }

            // 记录重试日志
            // refundLogService.recordLog(
            // refundRequest.getId(),
            // refundRequest.getRefundSn(),
            // RefundOperationType.MANUAL_RETRY,
            // "手动重试失败的退款",
            // "SUCCESS",
            // null,
            // null,
            // null,
            // operatorId,
            // operatorName
            // );

            // 调用处理退款
            return processRefund(refundRequestId, operatorId, operatorName);

        } catch (Exception e) {
            LOGGER.error("重试失败退款异常", e);
            return CommonResult.failed("重试失败退款异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Map<String, Object>> getRefundStatistics(Date startDate, Date endDate) {
        try {
            Map<String, Object> statistics = refundRequestDao.selectRefundStatistics(startDate, endDate);
            return CommonResult.success(statistics);
        } catch (Exception e) {
            LOGGER.error("获取退款统计异常", e);
            return CommonResult.failed("获取退款统计异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<List<Map<String, Object>>> getDailyRefundStatistics(Date startDate, Date endDate) {
        try {
            List<Map<String, Object>> dailyStats = refundRequestDao.selectDailyRefundStatistics(startDate, endDate);
            return CommonResult.success(dailyStats);
        } catch (Exception e) {
            LOGGER.error("获取每日退款统计异常", e);
            return CommonResult.failed("获取每日退款统计异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Integer> processTimeoutRefunds() {
        try {
            return CommonResult.success(0, "第三方退款已停用，无需处理超时退款");

        } catch (Exception e) {
            LOGGER.error("处理超时退款异常", e);
            return CommonResult.failed("处理超时退款异常: " + e.getMessage());
        }
    }

    /**
     * 生成退款单号
     */
    private String generateRefundSn() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "RF" + sdf.format(new Date()) + String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 退款成功后更新订单状态
     *
     * @param refundRequest 退款申请
     */
    private void updateOrderStatusOnRefundSuccess(RefundRequest refundRequest) {
        try {
            // 获取订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(refundRequest.getOrderId());
            if (order == null) {
                LOGGER.warn("退款成功但找不到对应订单，订单ID: {}", refundRequest.getOrderId());
                return;
            }

            BigDecimal paidAmount = order.getPayAmount() != null ? order.getPayAmount() : order.getTotalAmount();

            // 检查是否为全额退款：以实付金额为准，避免含运费订单被商品金额退款误关单。
            if (paidAmount != null && refundRequest.getRefundAmount().compareTo(paidAmount) >= 0) {
                // 全额退款，将订单状态更新为已关闭(4)
                OmsOrder updateOrder = new OmsOrder();
                updateOrder.setId(order.getId());
                updateOrder.setStatus(4); // 4->已关闭
                updateOrder.setModifyTime(new Date());

                int result = orderMapper.updateByPrimaryKeySelective(updateOrder);
                if (result > 0) {
                    LOGGER.info("全额退款成功，订单状态已更新为已关闭，订单ID: {}, 退款单号: {}",
                            order.getId(), refundRequest.getRefundSn());
                }
            } else {
                // 部分退款，订单状态保持不变，只记录日志
                LOGGER.info("部分退款成功，订单状态保持不变，订单ID: {}, 退款金额: {}, 订单总金额: {}",
                        order.getId(), refundRequest.getRefundAmount(), paidAmount);
            }

        } catch (Exception e) {
            LOGGER.error("退款成功后更新订单状态异常，退款单号: {}", refundRequest.getRefundSn(), e);
            // 不抛出异常，避免影响退款流程
        }
    }

    private int normalizePageNum(Integer pageNum) {
        if (pageNum == null || pageNum < 1) {
            return 1;
        }
        return pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }

    private <T> CommonPage<T> buildPage(List<T> list, int pageNum, int pageSize, long total) {
        CommonPage<T> page = new CommonPage<>();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal(total);
        page.setTotalPage((int) Math.ceil((double) total / pageSize));
        page.setList(list);
        return page;
    }
}
