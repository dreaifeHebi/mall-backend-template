package com.macro.mall.portal.domain.payment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * H5支付响应结果
 * @author macrozheng
 * @date 2025/7/26
 */
@ApiModel("H5支付响应结果")
public class H5PaymentResponse {

    @ApiModelProperty("是否成功")
    private Boolean success;

    @ApiModelProperty("支付记录ID")
    private Long paymentId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("支付渠道")
    private String paymentChannel;

    @ApiModelProperty("支付金额")
    private BigDecimal paymentAmount;

    @ApiModelProperty("支付URL")
    private String paymentUrl;

    @ApiModelProperty("支付状态")
    private String paymentStatus;

    @ApiModelProperty("支付Provider")
    private String provider;

    @ApiModelProperty("是否需要跳转外部支付页")
    private Boolean redirectRequired;

    @ApiModelProperty("错误信息")
    private String errorMessage;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
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

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Boolean getRedirectRequired() {
        return redirectRequired;
    }

    public void setRedirectRequired(Boolean redirectRequired) {
        this.redirectRequired = redirectRequired;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
