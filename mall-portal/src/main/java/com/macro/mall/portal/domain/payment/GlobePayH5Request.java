package com.macro.mall.portal.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GlobePay H5支付请求参数
 * @author macrozheng
 * @date 2025/7/26
 */
public class GlobePayH5Request {
    
    /**
     * 订单标题（最大长度128字符，超出自动截取）
     */
    private String description;
    
    /**
     * 金额，单位为货币最小单位，例如使用100表示100 JPY
     */
    private Integer price;
    
    /**
     * 币种代码，默认JPY，允许值: JPY, CNY
     */
    private String currency = "JPY";
    
    /**
     * 预授权标记，当前订单是否使用预授权模式，默认false
     */
    private Boolean preauth = false;
    
    /**
     * 支付渠道，大小写敏感，允许值: Alipay, Alipay+, Wechat, UnionPay
     */
    private String channel;
    
    /**
     * 支付通知url，详见支付通知api，不填则不会推送支付通知
     * notify_url
     */
    @JsonProperty("notify_url")
    private String notifyUrl;
    
    /**
     * 操作人员标识
     */
    private String operator;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getPreauth() {
        return preauth;
    }

    public void setPreauth(Boolean preauth) {
        this.preauth = preauth;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "GlobePayH5Request{" +
                "description='" + description + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                ", preauth=" + preauth +
                ", channel='" + channel + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", operator='" + operator + '\'' +
                '}';
    }
}
