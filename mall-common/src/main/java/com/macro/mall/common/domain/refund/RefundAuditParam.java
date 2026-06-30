package com.macro.mall.common.domain.refund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 退款审核请求参数
 * @author macrozheng
 * @date 2025/7/27
 */
@ApiModel(description = "退款审核请求参数")
public class RefundAuditParam {
    
    @ApiModelProperty(value = "退款申请ID", required = true)
    @NotNull(message = "退款申请ID不能为空")
    private Long refundRequestId;
    
    @ApiModelProperty(value = "审核状态", required = true, example = "APPROVED,REJECTED")
    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;
    
    @ApiModelProperty(value = "审核备注")
    @Size(max = 500, message = "审核备注长度不能超过500字符")
    private String auditNote;

    // Getters and Setters
    public Long getRefundRequestId() {
        return refundRequestId;
    }

    public void setRefundRequestId(Long refundRequestId) {
        this.refundRequestId = refundRequestId;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditNote() {
        return auditNote;
    }

    public void setAuditNote(String auditNote) {
        this.auditNote = auditNote;
    }
}
