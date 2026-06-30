package com.macro.mall.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GlobePay配置类
 * @author macrozheng
 * @date 2025/7/26
 */
@Component
@ConfigurationProperties(prefix = "globepay")
public class GlobePayConfig {
    
    /**
     * 生产环境域名
     */
    private String baseUrl = "https://pay.globepay.co.jp";
    
    /**
     * 商户编码，由4~6位大写字母或数字构成
     */
    private String partnerCode;
    
    /**
     * 系统为商户分配的开发校验码
     */
    private String credentialCode;
    
    /**
     * 支付通知URL
     */
    private String notifyUrl;
    
    /**
     * 支付成功后跳转URL
     */
    private String redirectUrl;
    
    /**
     * 信用卡绑卡成功后返回URL
     */
    private String creditCardReturnUrl;
    
    /**
     * 默认货币类型
     */
    private String defaultCurrency = "JPY";
    
    /**
     * 签名有效期（分钟）
     */
    private int signValidMinutes = 5;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getCredentialCode() {
        return credentialCode;
    }

    public void setCredentialCode(String credentialCode) {
        this.credentialCode = credentialCode;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getCreditCardReturnUrl() {
        return creditCardReturnUrl;
    }

    public void setCreditCardReturnUrl(String creditCardReturnUrl) {
        this.creditCardReturnUrl = creditCardReturnUrl;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public int getSignValidMinutes() {
        return signValidMinutes;
    }

    public void setSignValidMinutes(int signValidMinutes) {
        this.signValidMinutes = signValidMinutes;
    }

    @Override
    public String toString() {
        return "GlobePayConfig{" +
                "baseUrl='" + baseUrl + '\'' +
                ", partnerCode='" + partnerCode + '\'' +
                ", credentialCode='***'" +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", creditCardReturnUrl='" + creditCardReturnUrl + '\'' +
                ", defaultCurrency='" + defaultCurrency + '\'' +
                ", signValidMinutes=" + signValidMinutes +
                '}';
    }
}