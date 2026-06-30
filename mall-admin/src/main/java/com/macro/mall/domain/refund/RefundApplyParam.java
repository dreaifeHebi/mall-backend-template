package com.macro.mall.domain.refund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 退款申请请求参数
 * @author dreaifekks
 * @date 2025/10/13
 */
@ApiModel(description = "退款申请请求参数")
public class RefundApplyParam {

    @ApiModelProperty(value = "订单ID", required = true)
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @ApiModelProperty(value = "退款金额", required = true)
    @NotNull(message = "退款金额不能为空")
    @Positive(message = "退款金额必须大于0")
    private BigDecimal refundAmount;

    @ApiModelProperty(value = "退款原因", required = true)
    @NotBlank(message = "退款原因不能为空")
    @Size(max = 500, message = "退款原因长度不能超过500字符")
    private String refundReason;

    @ApiModelProperty(value = "退款原因类型：1-质量问题，2-发错货，3-不想要了，4-其他")
    private Integer refundReasonType;

    @ApiModelProperty(value = "退款说明")
    @Size(max = 1000, message = "退款说明长度不能超过1000字符")
    private String refundDescription;

    @ApiModelProperty(value = "联系电话")
    @Size(max = 20, message = "联系电话长度不能超过20字符")
    private String contactPhone;

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public Integer getRefundReasonType() {
        return refundReasonType;
    }

    public void setRefundReasonType(Integer refundReasonType) {
        this.refundReasonType = refundReasonType;
    }

    public String getRefundDescription() {
        return refundDescription;
    }

    public void setRefundDescription(String refundDescription) {
        this.refundDescription = refundDescription;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
