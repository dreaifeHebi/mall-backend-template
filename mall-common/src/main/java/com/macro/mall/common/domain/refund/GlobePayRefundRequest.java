package com.macro.mall.common.domain.refund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * GlobePay退款请求参数
 * @author dreaifekks
 * @date 2025/7/27
 */
@ApiModel(description = "GlobePay退款请求参数")
public class GlobePayRefundRequest {

    @ApiModelProperty(value = "商户订单号", required = true)
    private String merTransactionId;

    @ApiModelProperty(value = "GlobePay交易号", required = true)
    private String transactionId;

    @ApiModelProperty(value = "退款金额（分）", required = true)
    private Long refundAmount;

    @ApiModelProperty(value = "原订单金额（分）", required = true)
    private Long orderAmount;

    @ApiModelProperty(value = "退款原因")
    private String refundReason;

    @ApiModelProperty(value = "退款单号")
    private String refundSn;

    @ApiModelProperty(value = "通知地址")
    private String notifyUrl;

    // Getters and Setters
    public String getMerTransactionId() {
        return merTransactionId;
    }

    public void setMerTransactionId(String merTransactionId) {
        this.merTransactionId = merTransactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Long refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Long getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Long orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getRefundSn() {
        return refundSn;
    }

    public void setRefundSn(String refundSn) {
        this.refundSn = refundSn;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    @Override
    public String toString() {
        return "GlobePayRefundRequest{" +
                "merTransactionId='" + merTransactionId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", refundAmount=" + refundAmount +
                ", orderAmount=" + orderAmount +
                ", refundReason='" + refundReason + '\'' +
                ", refundSn='" + refundSn + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                '}';
    }
}
