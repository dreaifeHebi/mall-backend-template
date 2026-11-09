package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.refund.GlobePayRefundRequest;
import com.macro.mall.common.domain.refund.GlobePayRefundResponse;
import com.macro.mall.common.domain.refund.GlobePayRefundQueryResponse;
import com.macro.mall.common.service.GlobePayRefundService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobePay退款测试控制器
 * @author dreaifekks
 * @date 2025/10/13
 */
@RestController
@Api(tags = "AdminGlobePayRefundTestController", description = "GlobePay退款测试管理")
@RequestMapping("/globepay/refund/test")
public class AdminGlobePayRefundTestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminGlobePayRefundTestController.class);

    @Autowired
    private GlobePayRefundService globePayRefundService;

    @ApiOperation("测试GlobePay退款申请")
    @PostMapping("/apply")
    public CommonResult<Map<String, Object>> testRefundApply(
            @ApiParam("商户订单号") @RequestParam String orderId,
            @ApiParam("退款金额（分）") @RequestParam Long refundAmount,
            @ApiParam("退款原因") @RequestParam(required = false, defaultValue = "测试退款") String refundReason) {

        try {
            // 生成退款单号
            String refundSn = "REF" + System.currentTimeMillis();

            // 构建退款请求
            GlobePayRefundRequest request = new GlobePayRefundRequest();
            request.setMerTransactionId(orderId);          // 商户订单号
            request.setRefundAmount(refundAmount);         // 退款金额
            request.setRefundReason(refundReason);         // 退款原因
            request.setRefundSn(refundSn);                 // 退款单号
            request.setNotifyUrl("http://localhost:8080/portal/globepay/refund/notify"); // 通知地址

            LOGGER.info("=== 开始测试GlobePay退款申请 ===");
            LOGGER.info("订单号: {}", orderId);
            LOGGER.info("退款金额: {}分", refundAmount);
            LOGGER.info("退款单号: {}", refundSn);
            LOGGER.info("退款原因: {}", refundReason);

            // 调用退款接口
            GlobePayRefundResponse response = globePayRefundService.createRefund(request);

            LOGGER.info("=== GlobePay退款申请结果 ===");
            LOGGER.info("返回码: {}", response.getReturnCode());
            LOGGER.info("返回消息: {}", response.getReturnMsg());
            LOGGER.info("结果码: {}", response.getResultCode());
            LOGGER.info("GlobePay退款单号: {}", response.getRefundId());
            LOGGER.info("商户退款单号: {}", response.getPartnerRefundId());
            LOGGER.info("退款金额: {}", response.getAmount());
            LOGGER.info("币种: {}", response.getCurrency());

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", "SUCCESS".equals(response.getReturnCode()));
            result.put("message", "退款申请测试完成");
            result.put("refundSn", refundSn);
            result.put("orderId", orderId);
            result.put("refundAmount", refundAmount);
            result.put("globePayResponse", response);

            return CommonResult.success(result);

        } catch (Exception e) {
            LOGGER.error("测试GlobePay退款申请失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "退款申请测试失败: " + e.getMessage());
            return CommonResult.failed("退款申请测试失败: " + e.getMessage());
        }
    }

    @ApiOperation("测试GlobePay退款状态查询")
    @PostMapping("/query")
    public CommonResult<Map<String, Object>> testRefundQuery(
            @ApiParam("商户订单号") @RequestParam String orderId,
            @ApiParam("商户退款单号") @RequestParam String refundId) {

        try {
            LOGGER.info("=== 开始测试GlobePay退款状态查询 ===");
            LOGGER.info("订单号: {}", orderId);
            LOGGER.info("退款单号: {}", refundId);

            // 调用查询接口
            GlobePayRefundQueryResponse response = globePayRefundService.queryRefundStatus(orderId, refundId);

            LOGGER.info("=== GlobePay退款查询结果 ===");
            LOGGER.info("返回码: {}", response.getReturnCode());
            LOGGER.info("返回消息: {}", response.getReturnMsg());
            LOGGER.info("结果码: {}", response.getResultCode());
            LOGGER.info("GlobePay退款单号: {}", response.getRefundId());
            LOGGER.info("商户退款单号: {}", response.getPartnerRefundId());
            LOGGER.info("退款金额: {}", response.getAmount());
            LOGGER.info("币种: {}", response.getCurrency());

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", "SUCCESS".equals(response.getReturnCode()));
            result.put("message", "退款查询测试完成");
            result.put("orderId", orderId);
            result.put("refundId", refundId);
            result.put("globePayResponse", response);

            return CommonResult.success(result);

        } catch (Exception e) {
            LOGGER.error("测试GlobePay退款查询失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "退款查询测试失败: " + e.getMessage());
            return CommonResult.failed("退款查询测试失败: " + e.getMessage());
        }
    }

    @ApiOperation("一键测试GlobePay退款（申请+查询）")
    @PostMapping("/full-test")
    public CommonResult<Map<String, Object>> testFullRefund(
            @ApiParam("商户订单号") @RequestParam String orderId,
            @ApiParam("退款金额（分）") @RequestParam Long refundAmount,
            @ApiParam("退款原因") @RequestParam(required = false, defaultValue = "一键测试退款") String refundReason) {

        Map<String, Object> fullResult = new HashMap<>();

        try {
            // 生成退款单号
            String refundSn = "REF" + System.currentTimeMillis();

            LOGGER.info("=== 开始一键测试GlobePay退款 ===");
            LOGGER.info("订单号: {}", orderId);
            LOGGER.info("退款金额: {}分", refundAmount);
            LOGGER.info("退款单号: {}", refundSn);

            // 步骤1: 申请退款
            GlobePayRefundRequest request = new GlobePayRefundRequest();
            request.setMerTransactionId(orderId);
            request.setRefundAmount(refundAmount);
            request.setRefundReason(refundReason);
            request.setRefundSn(refundSn);
            request.setNotifyUrl("http://localhost:8080/portal/globepay/refund/notify");

            LOGGER.info("步骤1: 申请退款");
            GlobePayRefundResponse applyResponse = globePayRefundService.createRefund(request);
            fullResult.put("applyResponse", applyResponse);
            fullResult.put("applySuccess", "SUCCESS".equals(applyResponse.getReturnCode()));

            // 步骤2: 查询退款状态
            LOGGER.info("步骤2: 查询退款状态");
            GlobePayRefundQueryResponse queryResponse = globePayRefundService.queryRefundStatus(orderId, refundSn);
            fullResult.put("queryResponse", queryResponse);
            fullResult.put("querySuccess", "SUCCESS".equals(queryResponse.getReturnCode()));

            // 汇总结果
            fullResult.put("orderId", orderId);
            fullResult.put("refundSn", refundSn);
            fullResult.put("refundAmount", refundAmount);
            fullResult.put("message", "GlobePay退款一键测试完成");
            fullResult.put("overallSuccess",
                "SUCCESS".equals(applyResponse.getReturnCode()) && "SUCCESS".equals(queryResponse.getReturnCode()));

            LOGGER.info("=== GlobePay退款一键测试完成 ===");

            return CommonResult.success(fullResult);

        } catch (Exception e) {
            LOGGER.error("一键测试GlobePay退款失败", e);
            fullResult.put("success", false);
            fullResult.put("message", "一键测试失败: " + e.getMessage());
            fullResult.put("error", e.getMessage());
            return CommonResult.failed("一键测试失败: " + e.getMessage());
        }
    }
}
