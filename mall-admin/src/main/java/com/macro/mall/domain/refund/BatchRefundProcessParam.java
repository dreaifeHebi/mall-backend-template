package com.macro.mall.domain.refund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 批量退款处理请求参数
 * @author macrozheng
 * @date 2025/10/13
 */
@ApiModel(description = "批量退款处理请求参数")
public class BatchRefundProcessParam {
    
    @ApiModelProperty(value = "退款申请ID列表", required = true)
    @NotEmpty(message = "退款申请ID列表不能为空")
    private List<Long> refundRequestIds;
    
    @ApiModelProperty(value = "操作类型：AUDIT_APPROVED-批量审核通过，AUDIT_REJECTED-批量审核拒绝，PROCESS-批量处理，QUERY-批量查询状态", required = true)
    @NotNull(message = "操作类型不能为空")
    private String operationType;
    
    @ApiModelProperty(value = "操作备注")
    @Size(max = 500, message = "操作备注长度不能超过500字符")
    private String operationNote;
    
    @ApiModelProperty(value = "是否异步处理")
    private Boolean asyncProcess;

    // Getters and Setters
    public List<Long> getRefundRequestIds() {
        return refundRequestIds;
    }

    public void setRefundRequestIds(List<Long> refundRequestIds) {
        this.refundRequestIds = refundRequestIds;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationNote() {
        return operationNote;
    }

    public void setOperationNote(String operationNote) {
        this.operationNote = operationNote;
    }

    public Boolean getAsyncProcess() {
        return asyncProcess;
    }

    public void setAsyncProcess(Boolean asyncProcess) {
        this.asyncProcess = asyncProcess;
    }
}