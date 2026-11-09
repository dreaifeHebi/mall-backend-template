package com.macro.mall.domain.refund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 退款处理日志实体类 - 基于现有 refund_process_log 表
 * @author dreaifekks
 * @date 2025/10/13
 */
@ApiModel(description = "退款处理日志")
public class RefundProcessLog {

    @ApiModelProperty(value = "日志ID")
    private Long id;

    @ApiModelProperty(value = "退款申请ID")
    private Long refundRequestId;

    @ApiModelProperty(value = "退款单号")
    private String refundSn;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "操作状态")
    private String operationStatus;

    @ApiModelProperty(value = "操作人ID")
    private Long operatorId;

    @ApiModelProperty(value = "操作人姓名")
    private String operatorName;

    @ApiModelProperty(value = "操作备注")
    private String operationNote;

    @ApiModelProperty(value = "请求参数")
    private String requestParams;

    @ApiModelProperty(value = "响应数据")
    private String responseData;

    @ApiModelProperty(value = "错误信息")
    private String errorMessage;

    @ApiModelProperty(value = "操作者IP")
    private String operatorIp;

    @ApiModelProperty(value = "处理耗时(毫秒)")
    private Long processingTimeMs;

    @ApiModelProperty(value = "操作时间")
    private Date operationTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "备注")
    private String note;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRefundRequestId() {
        return refundRequestId;
    }

    public void setRefundRequestId(Long refundRequestId) {
        this.refundRequestId = refundRequestId;
    }

    public String getRefundSn() {
        return refundSn;
    }

    public void setRefundSn(String refundSn) {
        this.refundSn = refundSn;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperationNote() {
        return operationNote;
    }

    public void setOperationNote(String operationNote) {
        this.operationNote = operationNote;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getOperatorIp() {
        return operatorIp;
    }

    public void setOperatorIp(String operatorIp) {
        this.operatorIp = operatorIp;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
