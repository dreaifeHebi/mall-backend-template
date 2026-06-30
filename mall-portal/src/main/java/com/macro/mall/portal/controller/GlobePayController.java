package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.payment.GlobePayH5Request;
import com.macro.mall.portal.domain.payment.GlobePayH5Response;
import com.macro.mall.portal.domain.payment.H5PaymentRequest;
import com.macro.mall.portal.service.GlobePayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * GlobePay支付Controller
 * @author dreaifekks
 * @date 2025/7/27
 */
@Controller
@Api(tags = "GlobePayController")
@Tag(name = "GlobePayController", description = "GlobePay支付相关接口")
@RequestMapping("/payment/globePay")
public class GlobePayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobePayController.class);

    @Autowired
    private GlobePayService globePayService;

    @ApiOperation("创建信用卡绑卡请求 (Tokenize模式 - 步骤1)")
    @RequestMapping(value = "/creditCard", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<GlobePayH5Response> createCreditCardBinding(@Valid @RequestBody H5PaymentRequest request) {
        try {
            // 生成订单号
            String orderId = request.getOrderSn();
            if (orderId == null || orderId.trim().isEmpty()) {
                orderId = "GP" + System.currentTimeMillis();
            }

            // 转换为GlobePay请求格式
            GlobePayH5Request globePayRequest = new GlobePayH5Request();
            globePayRequest.setDescription(request.getSubject());
            globePayRequest.setPrice(request.getTotalAmount().intValue());
            globePayRequest.setChannel("CREDIT_CARD");
            globePayRequest.setNotifyUrl("http://localhost:8085/payment/h5/notify/globepay");

            LOGGER.info("创建信用卡绑卡请求: orderId={}, request={}", orderId, request);

            GlobePayH5Response response = globePayService.createCreditCardPayment(orderId, globePayRequest);

            if ("SUCCESS".equals(response.getReturnCode())) {
                return CommonResult.success(response, "绑卡请求创建成功，请完成信用卡绑定");
            } else {
                return CommonResult.failed(response.getReturnMsg());
            }

        } catch (Exception e) {
            LOGGER.error("创建信用卡绑卡请求失败: ", e);
            return CommonResult.failed("创建绑卡请求失败: " + e.getMessage());
        }
    }

    @ApiOperation("查询绑卡结果 (Tokenize模式 - 步骤2)")
    @RequestMapping(value = "/queryBindCard/{requestId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<GlobePayH5Response> queryBindCardResult(@PathVariable("requestId") String requestId) {
        try {
            LOGGER.info("查询绑卡结果: requestId={}", requestId);

            GlobePayH5Response response = globePayService.queryBindCardResult(requestId);

            if ("SUCCESS".equals(response.getReturnCode())) {
                return CommonResult.success(response, "绑卡结果查询成功");
            } else {
                return CommonResult.failed(response.getReturnMsg());
            }

        } catch (Exception e) {
            LOGGER.error("查询绑卡结果失败: requestId={}", requestId, e);
            return CommonResult.failed("查询绑卡结果失败: " + e.getMessage());
        }
    }

    @ApiOperation("使用Member Token创建支付 (Tokenize模式 - 步骤3)")
    @RequestMapping(value = "/tokenPay/{orderId}/{memberToken}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<GlobePayH5Response> createTokenizedPayment(
            @PathVariable("orderId") String orderId,
            @PathVariable("memberToken") String memberToken,
            @Valid @RequestBody H5PaymentRequest request) {
        try {
            // 转换为GlobePay请求格式
            GlobePayH5Request globePayRequest = new GlobePayH5Request();
            globePayRequest.setDescription(request.getSubject());
            globePayRequest.setPrice(request.getTotalAmount().intValue());
            globePayRequest.setChannel("CREDIT_CARD");
            globePayRequest.setNotifyUrl("http://localhost:8085/payment/h5/notify/globepay");

            LOGGER.info("创建Token支付: orderId={}, memberToken={}, request={}", orderId, memberToken, request);

            GlobePayH5Response response = globePayService.createTokenizedPayment(orderId, memberToken, globePayRequest);

            if ("SUCCESS".equals(response.getReturnCode())) {
                return CommonResult.success(response, "Token支付创建成功");
            } else {
                return CommonResult.failed(response.getReturnMsg());
            }

        } catch (Exception e) {
            LOGGER.error("创建Token支付失败: orderId={}, memberToken={}", orderId, memberToken, e);
            return CommonResult.failed("创建Token支付失败: " + e.getMessage());
        }
    }

    @ApiOperation("GlobePay支付成功回调页面")
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String paymentSuccess() {
        return "payment-success";
    }
}
