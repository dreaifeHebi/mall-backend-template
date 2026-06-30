package com.macro.mall.portal.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GlobePay H5支付响应
 * @author dreaifekks
 * @date 2025/7/26
 */
public class GlobePayH5Response {

    /**
     * 执行结果
     */
    @JsonProperty("return_code")
    private String returnCode;

    /**
     * SUCCESS表示创建订单成功，EXISTS表示订单已存在
     */
    @JsonProperty("result_code")
    private String resultCode;

    /**
     * 商户编码
     */
    @JsonProperty("partner_code")
    private String partnerCode;

    /**
     * 支付渠道
     */
    private String channel;

    /**
     * 商户注册全名
     */
    @JsonProperty("full_name")
    private String fullName;

    /**
     * 商户名称
     */
    @JsonProperty("partner_name")
    private String partnerName;

    /**
     * GlobePay订单ID，同时也是微信订单ID，最终支付成功的订单ID可能不同
     */
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 商户订单ID
     */
    @JsonProperty("partner_order_id")
    private String partnerOrderId;

    /**
     * 跳转URL
     */
    @JsonProperty("pay_url")
    private String payUrl;

    /**
     * 错误信息
     */
    @JsonProperty("return_msg")
    private String returnMsg;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPartnerOrderId() {
        return partnerOrderId;
    }

    public void setPartnerOrderId(String partnerOrderId) {
        this.partnerOrderId = partnerOrderId;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return "SUCCESS".equals(returnCode);
    }

    @Override
    public String toString() {
        return "GlobePayH5Response{" +
                "returnCode='" + returnCode + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", partnerCode='" + partnerCode + '\'' +
                ", channel='" + channel + '\'' +
                ", fullName='" + fullName + '\'' +
                ", partnerName='" + partnerName + '\'' +
                ", orderId='" + orderId + '\'' +
                ", partnerOrderId='" + partnerOrderId + '\'' +
                ", payUrl='" + payUrl + '\'' +
                ", returnMsg='" + returnMsg + '\'' +
                '}';
    }
}
