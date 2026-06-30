package com.macro.mall.portal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * H5支付配置类
 * @author macrozheng
 * @date 2025/7/26
 */
@Configuration
@ConfigurationProperties(prefix = "h5.payment")
public class H5PaymentConfig {

    private String provider = "LOCAL_SELF";
    private String localReturnUrl = "";
    private AlipayH5Config alipay = new AlipayH5Config();
    private WechatH5Config wechat = new WechatH5Config();
    private GlobePayConfig globepay = new GlobePayConfig();
    private StripeConfig stripe = new StripeConfig();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLocalReturnUrl() {
        return localReturnUrl;
    }

    public void setLocalReturnUrl(String localReturnUrl) {
        this.localReturnUrl = localReturnUrl;
    }

    public AlipayH5Config getAlipay() {
        return alipay;
    }

    public void setAlipay(AlipayH5Config alipay) {
        this.alipay = alipay;
    }

    public WechatH5Config getWechat() {
        return wechat;
    }

    public void setWechat(WechatH5Config wechat) {
        this.wechat = wechat;
    }

    public GlobePayConfig getGlobepay() {
        return globepay;
    }

    public void setGlobepay(GlobePayConfig globepay) {
        this.globepay = globepay;
    }

    public StripeConfig getStripe() {
        return stripe;
    }

    public void setStripe(StripeConfig stripe) {
        this.stripe = stripe;
    }

    /**
     * 支付宝H5支付配置
     */
    public static class AlipayH5Config {
        private String appId;
        private String merchantPrivateKey;
        private String alipayPublicKey;
        private String notifyUrl;
        private String returnUrl;
        private String signType = "RSA2";
        private String charset = "UTF-8";

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getMerchantPrivateKey() {
            return merchantPrivateKey;
        }

        public void setMerchantPrivateKey(String merchantPrivateKey) {
            this.merchantPrivateKey = merchantPrivateKey;
        }

        public String getAlipayPublicKey() {
            return alipayPublicKey;
        }

        public void setAlipayPublicKey(String alipayPublicKey) {
            this.alipayPublicKey = alipayPublicKey;
        }

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public void setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public String getSignType() {
            return signType;
        }

        public void setSignType(String signType) {
            this.signType = signType;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }
    }

    /**
     * 微信H5支付配置
     */
    public static class WechatH5Config {
        private String appId;
        private String mchId;
        private String apiKey;
        private String notifyUrl;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getMchId() {
            return mchId;
        }

        public void setMchId(String mchId) {
            this.mchId = mchId;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public void setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
        }
    }

    /**
     * GlobePay配置（信用卡支付）
     */
    public static class GlobePayConfig {
        private String baseUrl;
        private String partnerCode;
        private String credentialCode;
        private String notifyUrl;
        private String returnUrl;
        private String currency = "JPY";
        private Integer timeoutMinutes = 30;

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

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Integer getTimeoutMinutes() {
            return timeoutMinutes;
        }

        public void setTimeoutMinutes(Integer timeoutMinutes) {
            this.timeoutMinutes = timeoutMinutes;
        }
    }

    /**
     * Stripe Checkout配置占位。轻量模板默认不启用，生产接入时在 provider 非 LOCAL_SELF 时实现真实 SDK 调用。
     */
    public static class StripeConfig {
        private String secretKey;
        private String webhookSecret;
        private String successUrl;
        private String cancelUrl;

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getWebhookSecret() {
            return webhookSecret;
        }

        public void setWebhookSecret(String webhookSecret) {
            this.webhookSecret = webhookSecret;
        }

        public String getSuccessUrl() {
            return successUrl;
        }

        public void setSuccessUrl(String successUrl) {
            this.successUrl = successUrl;
        }

        public String getCancelUrl() {
            return cancelUrl;
        }

        public void setCancelUrl(String cancelUrl) {
            this.cancelUrl = cancelUrl;
        }
    }
}
