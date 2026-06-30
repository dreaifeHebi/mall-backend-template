package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 邮箱确认控制器
 * Created by mall on 2025/07/13.
 */
@Controller
@Api(tags = "EmailConfirmController")
@Tag(name = "EmailConfirmController", description = "邮箱确认管理")
public class EmailConfirmController {

    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("邮箱确认")
    @RequestMapping(value = "/confirm-email", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String> confirmEmail(@RequestParam String token) {
        try {
            memberService.confirmEmail(token);
            return CommonResult.success(null, "邮箱确认成功，您的账户已激活");
        } catch (Exception e) {
            return CommonResult.failed("邮箱确认失败: " + e.getMessage());
        }
    }
}
