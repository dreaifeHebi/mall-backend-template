package com.macro.mall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置类
 * Created by macro on 2025/07/22.
 */
@Configuration
@EnableScheduling
public class ScheduledTaskConfig {
    // 启用Spring的定时任务支持
    // @EnableScheduling注解会自动扫描带有@Scheduled注解的方法
}
