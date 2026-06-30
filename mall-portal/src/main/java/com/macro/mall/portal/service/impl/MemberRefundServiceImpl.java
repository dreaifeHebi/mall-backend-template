package com.macro.mall.portal.service.impl;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.refund.RefundApplyParam;
import com.macro.mall.common.domain.refund.RefundRequest;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.dao.refund.RefundRequestDao;
import com.macro.mall.portal.service.MemberRefundService;
import com.macro.mall.portal.service.refund.RefundStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 会员退款服务实现类
 * 
 * @author macrozheng
 * @date 2025/10/14
 */
@Service
public class MemberRefundServiceImpl implements MemberRefundService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberRefundServiceImpl.class);

    @Autowired
    private RefundRequestDao refundRequestDao;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Override
    @Transactional
    public CommonResult<RefundRequest> applyRefund(RefundApplyParam param, Long memberId, String memberName) {
        try {
            // 验证订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(param.getOrderId());
            if (order == null) {
                return CommonResult.failed("订单不存在");
            }

            // 验证订单归属
            if (!order.getMemberId().equals(memberId)) {
                return CommonResult.failed("无权限操作该订单");
            }

            // 验证订单状态（待发货、已发货、已完成的已支付订单才能申请退款）
            if (order.getStatus() == null || order.getStatus() < 1 || order.getStatus() > 3) {
                return CommonResult.failed("订单状态不允许申请退款");
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

            int result = refundRequestDao.insert(refundRequest);
            if (result > 0) {
                LOGGER.info("会员退款申请成功，退款单号：{}", refundSn);
                return CommonResult.success(refundRequest, "退款申请提交成功");
            } else {
                return CommonResult.failed("退款申请提交失败");
            }
        } catch (Exception e) {
            LOGGER.error("申请退款异常", e);
            return CommonResult.failed("申请退款异常: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CommonResult<Void> cancelRefund(Long refundRequestId, Long memberId, String memberName) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(refundRequestId);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }

            // 验证权限
            if (!refundRequest.getMemberId().equals(memberId)) {
                return CommonResult.failed("无权限操作该退款申请");
            }

            // 只有待审核状态的申请才能取消
            if (!RefundStatus.PENDING_AUDIT.getCode().equals(refundRequest.getStatus())) {
                return CommonResult.failed("当前状态不允许取消");
            }

            // 更新状态为已取消
            refundRequest.setStatus(RefundStatus.CANCELLED.getCode());
            refundRequest.setUpdateTime(new Date());

            int result = refundRequestDao.updateById(refundRequest);
            if (result > 0) {
                LOGGER.info("会员取消退款申请成功，退款单号：{}", refundRequest.getRefundSn());
                return CommonResult.success(null, "取消退款申请成功");
            } else {
                return CommonResult.failed("取消退款申请失败");
            }
        } catch (Exception e) {
            LOGGER.error("取消退款申请异常", e);
            return CommonResult.failed("取消退款申请异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<List<RefundRequest>> getMemberRefundList(Long memberId, String status,
            Integer pageNum, Integer pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<RefundRequest> refundList = refundRequestDao.selectByMemberId(
                    memberId, status, offset, pageSize);
            return CommonResult.success(refundList);
        } catch (Exception e) {
            LOGGER.error("获取会员退款列表异常", e);
            return CommonResult.failed("获取退款列表异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<RefundRequest> getMemberRefundDetail(Long refundRequestId, Long memberId) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(refundRequestId);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }

            // 验证权限
            if (!refundRequest.getMemberId().equals(memberId)) {
                return CommonResult.failed("无权限查看该退款申请");
            }

            return CommonResult.success(refundRequest);
        } catch (Exception e) {
            LOGGER.error("获取退款详情异常", e);
            return CommonResult.failed("获取退款详情异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<RefundRequest> queryRefundStatus(Long refundRequestId) {
        try {
            RefundRequest refundRequest = refundRequestDao.selectById(refundRequestId);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }

            // TODO: 这里可以调用第三方支付服务查询最新状态
            // 暂时直接返回数据库中的状态
            return CommonResult.success(refundRequest);
        } catch (Exception e) {
            LOGGER.error("查询退款状态异常", e);
            return CommonResult.failed("查询退款状态异常: " + e.getMessage());
        }
    }

    /**
     * 生成退款单号
     */
    private String generateRefundSn() {
        return "RF" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }
}
