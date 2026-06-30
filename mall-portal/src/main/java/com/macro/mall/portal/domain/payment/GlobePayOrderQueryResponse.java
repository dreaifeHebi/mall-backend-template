package com.macro.mall.portal.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * GlobePay订单查询响应
 * @author macrozheng
 * @date 2025/9/28
 */
@ApiModel("GlobePay订单查询响应")
public class GlobePayOrderQueryResponse {

    /**
     * 执行结果
     */
    @ApiModelProperty(value = "执行结果")
    @JsonProperty("return_code")
    private String returnCode;

    /**
     * 订单状态：PAYING、CREATE_FAIL、CLOSED、PAY_FAIL、PAY_SUCCESS、PARTIAL_REFUND、FULL_REFUND
     */
    @ApiModelProperty(value = "订单状态")
    @JsonProperty("result_code")
    private String resultCode;

    /**
     * GlobePay订单ID，同时也是微信订单ID，最终支付成功的订单ID可能不同
     */
    @ApiModelProperty(value = "GlobePay订单ID")
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 渠道方(微信、支付宝等)订单ID
     */
    @ApiModelProperty(value = "渠道方订单ID")
    @JsonProperty("channel_order_id")
    private String channelOrderId;

    /**
     * 商户订单ID
     */
    @ApiModelProperty(value = "商户订单ID")
    @JsonProperty("partner_order_id")
    private String partnerOrderId;

    /**
     * 订单金额，单位是货币最小面值单位
     */
    @ApiModelProperty(value = "订单金额")
    @JsonProperty("total_fee")
    private Integer totalFee;

    /**
     * 实际支付金额，单位是货币最小面值单位(目前等于订单金额，为卡券预留)
     */
    @ApiModelProperty(value = "实际支付金额")
    @JsonProperty("real_fee")
    private Integer realFee;

    /**
     * 交易时使用的汇率，1JPY=?CNY
     */
    @ApiModelProperty(value = "汇率")
    @JsonProperty("rate")
    private Double rate;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    @JsonProperty("customer_id")
    private String customerId;

    /**
     * 支付时间（yyyy-MM-dd HH:mm:ss，GMT+9）
     */
    @ApiModelProperty(value = "支付时间")
    @JsonProperty("pay_time")
    private String payTime;

    /**
     * 订单创建时间（最新订单为准）（yyyy-MM-dd HH:mm:ss，GMT+9）
     */
    @ApiModelProperty(value = "订单创建时间")
    @JsonProperty("create_time")
    private String createTime;

    /**
     * 币种，通常为JPY
     */
    @ApiModelProperty(value = "币种")
    @JsonProperty("currency")
    private String currency;

    /**
     * 支付渠道 Alipay|支付宝、Wechat|微信、Alipay+、UnionPay、UnionPayOnline
     */
    @ApiModelProperty(value = "支付渠道")
    @JsonProperty("channel")
    private String channel;

    /**
     * 当前订单是否是预授权订单
     */
    @ApiModelProperty(value = "预授权订单标记")
    @JsonProperty("preauth_flag")
    private Boolean preauthFlag;

    /**
     * 预授权状态：PENDING、AUTH_FAIL、AUTH、AUTH_PENDING、CAPTURED、CAPTURE_FAIL、CANCELED、CANCEL_FAIL
     */
    @ApiModelProperty(value = "预授权状态")
    @JsonProperty("preauth_status")
    private String preauthStatus;

    /**
     * 扣款时间，当预授权状态为CAPTURED时返回，（yyyy-MM-dd HH:mm:ss，GMT+9）
     */
    @ApiModelProperty(value = "扣款时间")
    @JsonProperty("preauth_capture_time")
    private String preauthCaptureTime;

    /**
     * 预授权有效期，预授权成功后返回，超出期限后会自动取消。（yyyy-MM-dd HH:mm:ss，GMT+9）
     */
    @ApiModelProperty(value = "预授权有效期")
    @JsonProperty("preauth_expire_time")
    private String preauthExpireTime;

    /**
     * 错误信息
     */
    @ApiModelProperty(value = "错误信息")
    @JsonProperty("return_msg")
    private String returnMsg;

    // Getters and Setters
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public String getPartnerOrderId() {
        return partnerOrderId;
    }

    public void setPartnerOrderId(String partnerOrderId) {
        this.partnerOrderId = partnerOrderId;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getRealFee() {
        return realFee;
    }

    public void setRealFee(Integer realFee) {
        this.realFee = realFee;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Boolean getPreauthFlag() {
        return preauthFlag;
    }

    public void setPreauthFlag(Boolean preauthFlag) {
        this.preauthFlag = preauthFlag;
    }

    public String getPreauthStatus() {
        return preauthStatus;
    }

    public void setPreauthStatus(String preauthStatus) {
        this.preauthStatus = preauthStatus;
    }

    public String getPreauthCaptureTime() {
        return preauthCaptureTime;
    }

    public void setPreauthCaptureTime(String preauthCaptureTime) {
        this.preauthCaptureTime = preauthCaptureTime;
    }

    public String getPreauthExpireTime() {
        return preauthExpireTime;
    }

    public void setPreauthExpireTime(String preauthExpireTime) {
        this.preauthExpireTime = preauthExpireTime;
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

    /**
     * 判断支付是否成功
     */
    public boolean isPaymentSuccess() {
        return "PAY_SUCCESS".equals(resultCode);
    }

    /**
     * 判断支付是否失败
     */
    public boolean isPaymentFailed() {
        return "PAY_FAIL".equals(resultCode) || "CREATE_FAIL".equals(resultCode);
    }

    /**
     * 判断支付是否进行中
     */
    public boolean isPaymentPending() {
        return "PAYING".equals(resultCode);
    }

    @Override
    public String toString() {
        return "GlobePayOrderQueryResponse{" +
                "returnCode='" + returnCode + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", orderId='" + orderId + '\'' +
                ", channelOrderId='" + channelOrderId + '\'' +
                ", partnerOrderId='" + partnerOrderId + '\'' +
                ", totalFee=" + totalFee +
                ", realFee=" + realFee +
                ", rate=" + rate +
                ", customerId='" + customerId + '\'' +
                ", payTime='" + payTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", currency='" + currency + '\'' +
                ", channel='" + channel + '\'' +
                ", preauthFlag=" + preauthFlag +
                ", preauthStatus='" + preauthStatus + '\'' +
                ", preauthCaptureTime='" + preauthCaptureTime + '\'' +
                ", preauthExpireTime='" + preauthExpireTime + '\'' +
                ", returnMsg='" + returnMsg + '\'' +
                '}';
    }
}