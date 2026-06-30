package com.macro.mall.portal.domain.payment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付记录实体类
 * @author macrozheng
 * @date 2025/7/26
 */
@ApiModel("支付记录")
public class PaymentRecord {

    @ApiModelProperty("支付记录ID")
    private Long id;

    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("支付渠道")
    private String paymentChannel;

    @ApiModelProperty("支付方式")
    private String paymentMethod;

    @ApiModelProperty("支付金额")
    private BigDecimal paymentAmount;

    @ApiModelProperty("货币")
    private String currency;

    @ApiModelProperty("支付状态：PENDING-待支付，SUCCESS-支付成功，FAILED-支付失败，CANCELLED-已取消")
    private String paymentStatus;

    @ApiModelProperty("第三方订单号")
    private String thirdPartyOrderId;

    @ApiModelProperty("第三方交易号")
    private String thirdPartyTradeNo;

    @ApiModelProperty("支付链接")
    private String paymentUrl;

    @ApiModelProperty("二维码链接")
    private String qrCodeUrl;

    @ApiModelProperty("请求参数")
    private String requestParams;

    @ApiModelProperty("支付响应")
    private String paymentResponse;

    @ApiModelProperty("通知响应")
    private String notifyResponse;

    @ApiModelProperty("失败原因")
    private String failureReason;

    @ApiModelProperty("支付时间")
    private Date paymentTime;

    @ApiModelProperty("通知时间")
    private Date notifyTime;

    @ApiModelProperty("过期时间")
    private Date expireTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getThirdPartyOrderId() {
        return thirdPartyOrderId;
    }

    public void setThirdPartyOrderId(String thirdPartyOrderId) {
        this.thirdPartyOrderId = thirdPartyOrderId;
    }

    public String getThirdPartyTradeNo() {
        return thirdPartyTradeNo;
    }

    public void setThirdPartyTradeNo(String thirdPartyTradeNo) {
        this.thirdPartyTradeNo = thirdPartyTradeNo;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(String paymentResponse) {
        this.paymentResponse = paymentResponse;
    }

    public String getNotifyResponse() {
        return notifyResponse;
    }

    public void setNotifyResponse(String notifyResponse) {
        this.notifyResponse = notifyResponse;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
