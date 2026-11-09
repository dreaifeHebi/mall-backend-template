package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.domain.refund.RefundApplyParam;
import com.macro.mall.domain.refund.RefundAuditParam;
import com.macro.mall.domain.refund.RefundRequest;

import com.macro.mall.service.refund.RefundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


/**
 * 退款管理控制器（管理员端）
 * @author dreaifekks
 * @date 2025/7/27
 */
@RestController
@Api(tags = "AdminRefundController", description = "管理员退款管理")
@RequestMapping("/admin/refund")
public class AdminRefundController {

    @Autowired
    private RefundService refundService;

    @ApiOperation("审核退款申请")
    @PostMapping("/audit")
    public CommonResult<RefundRequest> auditRefund(@Validated @RequestBody RefundAuditParam param,
                                                  HttpServletRequest request) {
        // TODO: 从JWT或Session中获取管理员ID和姓名
        Long auditorId = getAdminIdFromRequest(request);
        String auditorName = getAdminNameFromRequest(request);
        return refundService.auditRefund(param, auditorId, auditorName);
    }

    @ApiOperation("处理退款（发起第三方退款）")
    @PostMapping("/process/{refundRequestId}")
    public CommonResult<RefundRequest> processRefund(@ApiParam("退款申请ID") @PathVariable Long refundRequestId,
                                                    HttpServletRequest request) {
        Long operatorId = getAdminIdFromRequest(request);
        String operatorName = getAdminNameFromRequest(request);
        return refundService.processRefund(refundRequestId, operatorId, operatorName);
    }

    @ApiOperation("获取退款申请列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<RefundRequest>> getRefundList(
            @ApiParam("退款状态") @RequestParam(required = false) String status,
            @ApiParam("开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @ApiParam("结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @ApiParam("会员用户名") @RequestParam(required = false) String memberUsername,
            @ApiParam("订单号") @RequestParam(required = false) String orderSn,
            @ApiParam("退款单号") @RequestParam(required = false) String refundSn,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("订单id") @RequestParam(required = false) Long orderId) {
        CommonResult<CommonPage<RefundRequest>> result = refundService.getAdminRefundList(
            status, startDate, endDate, memberUsername, orderSn, refundSn, pageNum, pageSize, orderId);
        return result;
    }

    @ApiOperation("获取退款申请详情")
    @GetMapping("/{refundRequestId}")
    public CommonResult<RefundRequest> getRefundDetail(
            @ApiParam("退款申请ID") @PathVariable Long refundRequestId) {
        return refundService.getAdminRefundDetail(refundRequestId);
    }

    @ApiOperation("查询退款状态")
    @GetMapping("/status/{refundRequestId}")
    public CommonResult<RefundRequest> queryRefundStatus(
            @ApiParam("退款申请ID") @PathVariable Long refundRequestId) {
        return refundService.queryRefundStatus(refundRequestId);
    }

    @ApiOperation("系统自动查询待处理退款状态")
    @PostMapping("/auto-query")
    public CommonResult<Integer> autoQueryPendingRefunds() {
        return refundService.autoQueryPendingRefunds();
    }

    // ==================== 会员端退款API ====================

    @ApiOperation("会员申请退款")
    @PostMapping("/member/apply")
    public CommonResult<RefundRequest> memberApplyRefund(@Validated @RequestBody RefundApplyParam param,
                                                        HttpServletRequest request) {
        Long memberId = getMemberIdFromRequest(request);
        String memberName = getMemberNameFromRequest(request);
        return refundService.applyRefund(param, memberId);
    }

    @ApiOperation("会员取消退款申请")
    @PostMapping("/member/cancel/{refundRequestId}")
    public CommonResult<Void> memberCancelRefund(@ApiParam("退款申请ID") @PathVariable Long refundRequestId,
                                                HttpServletRequest request) {
        Long memberId = getMemberIdFromRequest(request);
        String memberName = getMemberNameFromRequest(request);
        return refundService.cancelRefund(refundRequestId, memberId, null, memberName);
    }

    @ApiOperation("获取会员退款申请列表")
    @GetMapping("/member/list")
    public CommonResult<CommonPage<RefundRequest>> getMemberRefundList(
            @ApiParam("退款状态") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long memberId = getMemberIdFromRequest(request);
        CommonResult<CommonPage<RefundRequest>> result = refundService.getMemberRefundList(memberId, status, pageNum, pageSize);
        return result;
    }

    @ApiOperation("获取会员退款申请详情")
    @GetMapping("/member/{refundRequestId}")
    public CommonResult<RefundRequest> getMemberRefundDetail(
            @ApiParam("退款申请ID") @PathVariable Long refundRequestId,
            HttpServletRequest request) {
        Long memberId = getMemberIdFromRequest(request);
        return refundService.getRefundDetail(refundRequestId, memberId);
    }

    /**
     * 从请求中获取管理员ID
     * TODO: 实际项目中应该从JWT token或session中获取
     */
    private Long getAdminIdFromRequest(HttpServletRequest request) {
        String adminIdStr = request.getHeader("Admin-Id");
        if (adminIdStr != null) {
            return Long.parseLong(adminIdStr);
        }
        return 1L; // 默认管理员ID
    }

    /**
     * 从请求中获取管理员姓名
     * TODO: 实际项目中应该从JWT token或session中获取
     */
    private String getAdminNameFromRequest(HttpServletRequest request) {
        String adminName = request.getHeader("Admin-Name");
        if (adminName != null) {
            return adminName;
        }
        return "admin"; // 默认管理员姓名
    }

    /**
     * 从请求中获取会员ID
     */
    private Long getMemberIdFromRequest(HttpServletRequest request) {
        String memberIdStr = request.getHeader("Member-Id");
        if (memberIdStr != null) {
            return Long.parseLong(memberIdStr);
        }
        return 1L; // 默认会员ID
    }

    /**
     * 从请求中获取会员姓名
     */
    private String getMemberNameFromRequest(HttpServletRequest request) {
        String memberName = request.getHeader("Member-Name");
        if (memberName != null) {
            return memberName;
        }
        return "member"; // 默认会员姓名
    }
}
