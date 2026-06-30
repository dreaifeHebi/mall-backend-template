package com.macro.mall.portal.controller;

import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮箱验证测试控制器
 * 仅用于开发测试，生产环境应删除
 */
@RestController
public class EmailVerificationTestController {
    
    @Autowired
    private UmsMemberService memberService;
    
    @Autowired
    private RedisService redisService;
    
    /**
     * 创建测试验证token
     */
    @GetMapping("/test/createEmailToken")
    public String createTestToken(@RequestParam Long userId) {
        String testToken = "test-token-" + System.currentTimeMillis();
        redisService.set("email_verification:" + testToken, userId, 3600L);
        return "Test token created: " + testToken + " for user: " + userId;
    }
    
    /**
     * 测试邮箱验证
     */
    @GetMapping("/test/confirmEmail")
    public String testConfirmEmail(@RequestParam String token) {
        try {
            memberService.confirmEmail(token);
            return "Email verification successful!";
        } catch (Exception e) {
            return "Email verification failed: " + e.getMessage();
        }
    }
}
