package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.refund.RefundApplyParam;
import com.macro.mall.common.domain.refund.RefundRequest;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.service.MemberRefundService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 退款管理控制器（会员端）
 * @author dreaifekks
 * @date 2025/7/27
 */
@RestController
@Api(tags = "MemberRefundController", description = "会员退款管理")
@RequestMapping("/refund")
public class MemberRefundController {

    @Autowired
    private MemberRefundService memberRefundService;

    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("申请退款")
    @PostMapping("/apply")
    public CommonResult<RefundRequest> applyRefund(@Validated @RequestBody RefundApplyParam param) {
        UmsMember currentMember = memberService.getCurrentMember();
        return memberRefundService.applyRefund(param, currentMember.getId(), currentMember.getUsername());
    }

    @ApiOperation("取消退款申请")
    @PostMapping("/cancel/{refundRequestId}")
    public CommonResult<Void> cancelRefund(@ApiParam("退款申请ID") @PathVariable Long refundRequestId) {
        UmsMember currentMember = memberService.getCurrentMember();
        return memberRefundService.cancelRefund(refundRequestId, currentMember.getId(), currentMember.getUsername());
    }

    @ApiOperation("获取我的退款申请列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<RefundRequest>> getRefundList(
            @ApiParam("退款状态") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        UmsMember currentMember = memberService.getCurrentMember();
        CommonResult<List<RefundRequest>> result = memberRefundService.getMemberRefundList(
            currentMember.getId(), status, pageNum, pageSize);

        if (result.getCode() == 200) {
            return CommonResult.success(CommonPage.restPage(result.getData()));
        }
        return CommonResult.failed(result.getMessage());
    }

    @ApiOperation("获取退款申请详情")
    @GetMapping("/{refundRequestId}")
    public CommonResult<RefundRequest> getRefundDetail(
            @ApiParam("退款申请ID") @PathVariable Long refundRequestId) {
        UmsMember currentMember = memberService.getCurrentMember();
        return memberRefundService.getMemberRefundDetail(refundRequestId, currentMember.getId());
    }

    @ApiOperation("查询退款状态")
    @GetMapping("/status/{refundRequestId}")
    public CommonResult<RefundRequest> queryRefundStatus(
            @ApiParam("退款申请ID") @PathVariable Long refundRequestId) {
        // 验证会员权限
        UmsMember currentMember = memberService.getCurrentMember();
        CommonResult<RefundRequest> detailResult = memberRefundService.getMemberRefundDetail(refundRequestId, currentMember.getId());
        if (detailResult.getCode() != 200) {
            return detailResult;
        }

        return memberRefundService.queryRefundStatus(refundRequestId);
    }
}
