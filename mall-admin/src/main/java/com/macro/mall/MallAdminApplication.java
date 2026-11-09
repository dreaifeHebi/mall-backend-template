package com.macro.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用启动入口
 * Created by macro on 2018/4/26.
 */
@SpringBootApplication(scanBasePackages = "com.macro.mall")
@EnableScheduling
public class MallAdminApplication {
    public static void main(String[] args) {
        // 针对日文系统环境设置UTF-8编码
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        System.setProperty("console.encoding", "UTF-8");
        System.setProperty("user.language", "en");
        System.setProperty("user.country", "US");
        
        // 设置JVM默认字符集为UTF-8
        System.setProperty("java.awt.headless", "true");
        
        SpringApplication.run(MallAdminApplication.class, args);
    }
}
