package com.macro.mall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * 邮件服务配置
 * Created by macro on 2025/7/13.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mall.email")
public class EmailConfig {
    
    /**
     * 是否启用邮件服务
     */
    private boolean enabled = false;
    
    /**
     * 邮件发送模式: dev-开发模式(仅日志), prod-生产模式(真实发送)
     */
    private String mode = "dev";
    
    /**
     * 邮件发送失败重试次数
     */
    private int retryCount = 3;
    
    /**
     * 邮件发送超时时间(秒)
     */
    private int timeout = 30;
    
    /**
     * 邮件模板缓存时间(分钟)
     */
    private int templateCacheMinutes = 30;
}
