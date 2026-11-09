package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * H5支付测试页面Controller
 * @author dreaifekks
 * @date 2025/7/26
 */
@Controller
@Api(tags = "H5PaymentTestController")
@Tag(name = "H5PaymentTestController", description = "H5支付测试页面")
@RequestMapping("/payment/test")
public class H5PaymentTestController {

    @ApiOperation("支付测试页面")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public String paymentTestPage() {
        return "payment/test";
    }

    @ApiOperation("支付成功页面")
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String paymentSuccessPage() {
        return "payment/success";
    }

    @ApiOperation("支付失败页面")
    @RequestMapping(value = "/failed", method = RequestMethod.GET)
    public String paymentFailedPage() {
        return "payment/failed";
    }

    @ApiOperation("获取测试数据")
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getTestData() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("orderId", 123456L);
        testData.put("orderSn", "ORDER_" + System.currentTimeMillis());
        testData.put("totalAmount", 100.50);
        testData.put("subject", "商品购买测试");
        testData.put("currency", "JPY");

        return CommonResult.success(testData, "获取测试数据成功");
    }
}
