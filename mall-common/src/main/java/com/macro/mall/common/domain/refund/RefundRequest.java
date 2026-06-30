package com.macro.mall.common.domain.refund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退款申请实体类
 * @author macrozheng
 * @date 2025/7/27
 */
@ApiModel(description = "退款申请")
public class RefundRequest {
    
    @ApiModelProperty(value = "退款申请ID")
    private Long id;
    
    @ApiModelProperty(value = "退款单号")
    private String refundSn;
    
    @ApiModelProperty(value = "订单ID")
    private Long orderId;
    
    @ApiModelProperty(value = "订单号")
    private String orderSn;
    
    @ApiModelProperty(value = "支付记录ID")
    private Long paymentRecordId;
    
    @ApiModelProperty(value = "会员ID")
    private Long memberId;
    
    @ApiModelProperty(value = "会员用户名")
    private String memberUsername;
    
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;
    
    @ApiModelProperty(value = "退款原因")
    private String refundReason;
    
    @ApiModelProperty(value = "退款状态")
    private String status;
    
    @ApiModelProperty(value = "申请时间")
    private Date applyTime;
    
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
    
    @ApiModelProperty(value = "审核人ID")
    private Long auditorId;
    
    @ApiModelProperty(value = "审核人姓名")
    private String auditorName;
    
    @ApiModelProperty(value = "审核备注")
    private String auditNote;
    
    @ApiModelProperty(value = "第三方退款单号")
    private String thirdPartyRefundId;
    
    @ApiModelProperty(value = "退款完成时间")
    private Date refundTime;
    
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefundSn() {
        return refundSn;
    }

    public void setRefundSn(String refundSn) {
        this.refundSn = refundSn;
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

    public Long getPaymentRecordId() {
        return paymentRecordId;
    }

    public void setPaymentRecordId(Long paymentRecordId) {
        this.paymentRecordId = paymentRecordId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public String getAuditNote() {
        return auditNote;
    }

    public void setAuditNote(String auditNote) {
        this.auditNote = auditNote;
    }

    public String getThirdPartyRefundId() {
        return thirdPartyRefundId;
    }

    public void setThirdPartyRefundId(String thirdPartyRefundId) {
        this.thirdPartyRefundId = thirdPartyRefundId;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
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
