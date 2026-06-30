package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.payment.H5PaymentAdminConfirmRequest;
import com.macro.mall.portal.domain.payment.H5PaymentRequest;
import com.macro.mall.portal.domain.payment.H5PaymentResponse;
import com.macro.mall.portal.domain.payment.PaymentMethod;
import com.macro.mall.portal.domain.payment.PaymentRecord;
import com.macro.mall.portal.service.H5PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5支付Controller
 * @author macrozheng
 * @date 2025/7/26
 */
@Controller
@Api(tags = "H5PaymentController")
@Tag(name = "H5PaymentController", description = "H5支付相关接口")
@RequestMapping("/payment/h5")
public class H5PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(H5PaymentController.class);

    @Autowired
    private H5PaymentService h5PaymentService;

    @ApiOperation("创建H5支付订单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<H5PaymentResponse> createPayment(@Valid @RequestBody H5PaymentRequest request) {
        try {
            // 详细请求日志
            LOGGER.info("=== H5支付订单创建API调用开始 ===");
            LOGGER.info("请求参数: {}", request);
            LOGGER.info("订单号: {}", request.getOrderSn());
            LOGGER.info("支付渠道: {}", request.getPaymentChannel());
            LOGGER.info("支付金额: {}", request.getTotalAmount());
            
            H5PaymentResponse response = h5PaymentService.createPayment(request);
            
            LOGGER.info("=== H5支付订单创建API响应 ===");
            LOGGER.info("响应结果: {}", response);
            LOGGER.info("创建状态: {}", response.getSuccess() ? "成功" : "失败");
            
            if (response.getSuccess()) {
                LOGGER.info("=== H5支付订单创建API调用成功 ===");
                return CommonResult.success(response, "支付订单创建成功");
            } else {
                LOGGER.error("=== H5支付订单创建API调用失败 ===");
                LOGGER.error("失败原因: {}", response.getErrorMessage());
                return CommonResult.failed(response.getErrorMessage());
            }
        } catch (Exception e) {
            LOGGER.error("=== H5支付订单创建API异常 ===");
            LOGGER.error("请求参数: {}", request);
            LOGGER.error("异常类型: {}", e.getClass().getSimpleName());
            LOGGER.error("异常信息: {}", e.getMessage());
            LOGGER.error("异常堆栈: ", e);
            LOGGER.error("=== H5支付订单创建API异常结束 ===");
            return CommonResult.failed("创建支付订单失败: " + e.getMessage());
        }
    }

    @ApiOperation("查询支付状态")
    @RequestMapping(value = "/status/{orderSn}/{paymentChannel}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PaymentRecord> queryPaymentStatus(@PathVariable String orderSn, 
                                                         @PathVariable String paymentChannel) {
        try {
            PaymentRecord record = h5PaymentService.queryPaymentStatus(orderSn, paymentChannel);
            if (record != null) {
                return CommonResult.success(record, "查询成功");
            } else {
                return CommonResult.failed("未找到支付记录");
            }
        } catch (Exception e) {
            LOGGER.error("查询支付状态失败: ", e);
            return CommonResult.failed("查询支付状态失败: " + e.getMessage());
        }
    }

    @ApiOperation("取消支付")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Boolean> cancelPayment(@RequestParam String orderSn, 
                                              @RequestParam String paymentChannel) {
        try {
            boolean result = h5PaymentService.cancelPayment(orderSn, paymentChannel);
            if (result) {
                return CommonResult.success(true, "取消支付成功");
            } else {
                return CommonResult.failed("取消支付失败");
            }
        } catch (Exception e) {
            LOGGER.error("取消支付失败: ", e);
            return CommonResult.failed("取消支付失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取支付方式列表")
    @RequestMapping(value = "/methods", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PaymentMethod>> getPaymentMethods(@RequestParam(required = false) String type) {
        try {
            List<PaymentMethod> methods = h5PaymentService.getPaymentMethods(type);
            return CommonResult.success(methods, "获取支付方式列表成功");
        } catch (Exception e) {
            LOGGER.error("获取支付方式列表失败: ", e);
            return CommonResult.failed("获取支付方式列表失败: " + e.getMessage());
        }
    }

    @ApiOperation("管理员确认支付")
    @PostMapping("/admin/confirm")
    @ResponseBody
    public CommonResult<PaymentRecord> confirmPaymentByAdmin(@Valid @RequestBody H5PaymentAdminConfirmRequest request) {
        try {
            PaymentRecord record = h5PaymentService.confirmPaymentByAdmin(request);
            return CommonResult.success(record, "支付确认成功");
        } catch (Exception e) {
            LOGGER.error("管理员确认支付失败: ", e);
            return CommonResult.failed("支付确认失败: " + e.getMessage());
        }
    }

    @ApiOperation("支付宝异步通知")
    @RequestMapping(value = "/notify/alipay", method = RequestMethod.POST)
    @ResponseBody
    public String alipayNotify(HttpServletRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }
            
            LOGGER.info("接收到支付宝通知: {}", params);
            return h5PaymentService.handleAlipayNotify(params);
        } catch (Exception e) {
            LOGGER.error("处理支付宝通知失败: ", e);
            return "failure";
        }
    }

    @ApiOperation("微信异步通知")
    @RequestMapping(value = "/notify/wechat", method = RequestMethod.POST)
    @ResponseBody
    public String wechatNotify(@RequestBody String xmlData) {
        try {
            LOGGER.info("接收到微信通知: {}", xmlData);
            return h5PaymentService.handleWechatNotify(xmlData);
        } catch (Exception e) {
            LOGGER.error("处理微信通知失败: ", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>";
        }
    }

    @ApiOperation("GlobePay异步通知")
    @RequestMapping(value = "/notify/globepay", method = RequestMethod.POST)
    @ResponseBody
    public String globePayNotify(HttpServletRequest request, @RequestBody(required = false) String jsonBody) {
        try {
            LOGGER.info("=== 接收到GlobePay异步通知 ===");
            LOGGER.info("Content-Type: {}", request.getContentType());
            LOGGER.info("请求体: {}", jsonBody);
            
            // 判断是JSON格式还是表单格式
            if (jsonBody != null && !jsonBody.trim().isEmpty() && 
                request.getContentType() != null && request.getContentType().contains("application/json")) {
                // JSON格式通知
                LOGGER.info("处理JSON格式通知");
                return h5PaymentService.handleGlobePayJsonNotify(jsonBody);
            } else {
                // 表单格式通知（兼容旧版本）
                LOGGER.info("处理表单格式通知");
                Map<String, String> params = new HashMap<>();
                Map<String, String[]> requestParams = request.getParameterMap();
                for (String name : requestParams.keySet()) {
                    params.put(name, request.getParameter(name));
                }
                
                LOGGER.info("接收到GlobePay表单通知: {}", params);
                return h5PaymentService.handleGlobePayNotify(params);
            }
        } catch (Exception e) {
            LOGGER.error("处理GlobePay通知失败: ", e);
            return "failure";
        }
    }
}
