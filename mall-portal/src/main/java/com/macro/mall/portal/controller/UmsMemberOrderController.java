package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.EmailNotificationService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.domain.OrderAddressUpdateParam;
import com.macro.mall.model.UmsMember;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * 用户订单地址修改Controller
 * Created by macro on 2025/7/13.
 */
@RestController
@Api(tags = "UmsMemberOrderController")
@Tag(name = "UmsMemberOrderController", description = "用户订单管理")
@RequestMapping("/member/order")
public class UmsMemberOrderController {

    @Autowired
    @Qualifier("portalEmailNotificationService")
    private EmailNotificationService emailNotificationService;
    
    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("修改订单收货地址")
    @RequestMapping(value = "/updateAddress", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Object> updateOrderAddress(@RequestBody OrderAddressUpdateParam param) {
        try {
            UmsMember currentMember = memberService.getCurrentMember();
            
            // 将订单ID转换为Long类型
            Long orderId = Long.parseLong(param.getOrderId());
            
            // 格式化地址信息
            OrderAddressUpdateParam.AddressInfo addressInfo = param.getNewAddress();
            String formattedAddress = String.format("%s %s %s %s", 
                addressInfo.getReceiverProvince(),
                addressInfo.getReceiverCity(),
                addressInfo.getReceiverRegion(),
                addressInfo.getReceiverDetailAddress());
            
            // 发送订单修改通知邮件
            emailNotificationService.sendOrderModificationEmail(orderId, currentMember.getId(), formattedAddress);
            
            return CommonResult.success(null, "地址修改成功，通知邮件已发送");
        } catch (Exception e) {
            return CommonResult.failed("地址修改失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("测试发送邮件")
    @RequestMapping(value = "/testEmail", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Object> testEmail(@RequestParam Integer templateType,
                                  @RequestParam(required = false) Long orderId,
                                  @RequestParam(required = false) String trackingNumber,
                                  @RequestParam(required = false) String carrier,
                                  @RequestParam(required = false) String trackingUrl) {
        try {
            UmsMember currentMember = memberService.getCurrentMember();
            
            switch (templateType) {
                case 0:
                    // 订单确认邮件
                    if (orderId != null) {
                        emailNotificationService.sendOrderConfirmationEmail(orderId, currentMember.getId());
                    }
                    break;
                case 1:
                    // 国际单号通知邮件
                    if (orderId != null && trackingNumber != null) {
                        emailNotificationService.sendTrackingNumberEmail(orderId, currentMember.getId(), 
                            trackingNumber, carrier != null ? carrier : "DHL", 
                            trackingUrl != null ? trackingUrl : "http://tracking.example.com");
                    }
                    break;
                case 2:
                    // 注册确认邮件
                    emailNotificationService.sendRegistrationConfirmationEmail(currentMember.getId(), 
                        "http://localhost:8085/confirm-email?token=test123");
                    break;
                case 3:
                    // 订单修改邮件
                    if (orderId != null) {
                        emailNotificationService.sendOrderModificationEmail(orderId, currentMember.getId(), 
                            "北京市朝阳区测试大街123号");
                    }
                    break;
                case 4:
                    // 密码重置邮件
                    emailNotificationService.sendPasswordResetEmail(currentMember.getId(), 
                        "http://localhost:8085/reset-password?token=test123");
                    break;
                default:
                    return CommonResult.failed("无效的模板类型");
            }
            
            return CommonResult.success(null, "测试邮件发送成功");
        } catch (Exception e) {
            return CommonResult.failed("测试邮件发送失败: " + e.getMessage());
        }
    }
}
