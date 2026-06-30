package com.macro.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CSV导出配置
 * Created on 2025/7/3.
 */
@Data
@Component
@ConfigurationProperties(prefix = "mall.csv")
public class CsvExportConfig {
    
    /**
     * CSV文件名前缀
     */
    private String orderFilePrefix = "Order";
    
    /**
     * CSV文件名后缀
     */
    private String fileSuffix = ".csv";
    
    /**
     * 字符编码
     */
    private String encoding = "UTF-8";
    
    /**
     * 分隔符
     */
    private String delimiter = ",";
    
    /**
     * 换行符
     */
    private String lineBreak = "\r\n";
    
    /**
     * 字段包裹符
     */
    private String wrapper = "\"";
    
    /**
     * 头部标识符
     */
    private String headerIdentifier = "START";
    
    /**
     * 尾部标识符
     */
    private String footerIdentifier = "END";
}
