package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.dto.MemberRegisterParam;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * 会员管理Controller
 * Created by macro on 2018/8/3.
 */
@Controller
@Api(tags = "UmsMemberController")
@Tag(name = "UmsMemberController", description = "会员登录注册管理")
@RequestMapping("/sso")
public class UmsMemberController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("会员注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam(required = false) String telephone,
                                 @RequestParam String email) {
        memberService.register(username, password, telephone, email);
        return CommonResult.success(null,"注册成功");
    }

    @ApiOperation("会员注册（JSON格式）")
    @RequestMapping(value = "/register/json", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult registerWithJson(@RequestBody MemberRegisterParam registerParam) {
        memberService.register(registerParam.getUsername(), registerParam.getPassword(), 
                             registerParam.getTelephone(), registerParam.getEmail());
        return CommonResult.success(null,"注册成功");
    }

    @ApiOperation("会员登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestParam String username,
                              @RequestParam String password) {
        String token = memberService.login(username, password);
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation("获取会员信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult info(Principal principal) {
        if(principal==null){
            return CommonResult.unauthorized(null);
        }
        UmsMember member = memberService.getCurrentMember();
        return CommonResult.success(member);
    }

    @ApiOperation("发送密码重置邮件")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult sendResetPasswordEmail(@RequestParam String email) {
        try {
            memberService.sendResetEmail(email);
            return CommonResult.success(null, "密码重置邮件已发送");
        } catch (Exception e) {
            return CommonResult.failed("邮件发送失败: " + e.getMessage());
        }
    }

    @ApiOperation("重置密码")
    @RequestMapping(value = "/resetPassword/confirm", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> resetPassword(@RequestParam String token,
                                      @RequestParam String newPassword) {
        try {
            memberService.resetPassword(token, newPassword);
            return CommonResult.success(null, "密码重置成功");
        } catch (Exception e) {
            return CommonResult.failed("密码重置失败: " + e.getMessage());
        }
    }

    @ApiOperation("修改密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(Principal principal,
                                       @RequestParam String oldPassword,
                                       @RequestParam String newPassword) {
        if (principal == null) {
            return CommonResult.unauthorized(null);
        }
        try {
            memberService.changePassword(oldPassword, newPassword);
            return CommonResult.success(null, "密码修改成功");
        } catch (Exception e) {
            if (e.getMessage().contains("原密码错误")) {
                return CommonResult.failed("旧密码错误");
            }
            return CommonResult.failed("密码修改失败: " + e.getMessage());
        }
    }

    @ApiOperation("发送邮箱验证邮件")
    @RequestMapping(value = "/sendEmailVerification", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> sendEmailVerification(@RequestParam String email) {
        try {
            memberService.sendEmailVerification(email);
            return CommonResult.success(null, "邮箱验证邮件已发送，请检查您的邮箱");
        } catch (Exception e) {
            return CommonResult.failed("邮件发送失败: " + e.getMessage());
        }
    }

    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, String>> refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = memberService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation("邮箱验证确认")
    @RequestMapping(value = "/email/confirm", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> confirmEmailForFrontend(@RequestParam String token) {
        try {
            memberService.confirmEmail(token);
            return CommonResult.success("邮箱验证成功");
        } catch (Exception e) {
            return CommonResult.failed("邮箱验证失败: " + e.getMessage());
        }
    }

    @ApiOperation("更新会员信息")
    @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> updateMemberInfo(Principal principal,
                                                  @RequestParam(required = false) String nickname,
                                                  @RequestParam(required = false) String phone) {
        if (principal == null) {
            return CommonResult.unauthorized(null);
        }
        try {
            memberService.updateMemberInfo(nickname, phone);
            return CommonResult.success(null, "会员信息更新成功");
        } catch (Exception e) {
            return CommonResult.failed("会员信息更新失败: " + e.getMessage());
        }
    }
}
