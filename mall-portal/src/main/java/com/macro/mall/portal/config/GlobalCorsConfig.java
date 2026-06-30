package com.macro.mall.portal.config;

import org.springframework.context.annotation.Configuration;

/**
 * 全局跨域相关配置
 * Created by macro on 2019/7/27.
 * 
 * 注意：当使用 Nginx 反向代理时，CORS 应该在 Nginx 层面处理，
 * 避免重复的 CORS 头导致浏览器报错
 */
@Configuration
public class GlobalCorsConfig {

    /**
     * 允许跨域调用的过滤器
     * 注释掉以避免与 Nginx CORS 配置冲突
     */
    // @Bean
    // public CorsFilter corsFilter() {
    //     CorsConfiguration config = new CorsConfiguration();
    //     //允许所有域名进行跨域调用
    //     config.addAllowedOriginPattern("*");
    //     //允许跨越发送cookie
    //     config.setAllowCredentials(true);
    //     //放行全部原始头信息
    //     config.addAllowedHeader("*");
    //     //允许所有请求方法跨域调用
    //     config.addAllowedMethod("*");
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config);
    //     return new CorsFilter(source);
    // }
}
