package com.macro.mall.common.domain.refund;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * GlobePay退款查询响应结果
 * @author macrozheng
 * @date 2025/7/27
 */
@ApiModel(description = "GlobePay退款查询响应结果")
public class GlobePayRefundQueryResponse {
    
    @ApiModelProperty(value = "执行结果")
    @JsonProperty("return_code")
    private String returnCode;
    
    @ApiModelProperty(value = "返回消息")
    @JsonProperty("return_msg")
    private String returnMsg;
    
    @ApiModelProperty(value = "结果码")
    @JsonProperty("result_code") 
    private String resultCode;
    
    @ApiModelProperty(value = "GlobePay退款单号")
    @JsonProperty("refund_id")
    private String refundId;
    
    @ApiModelProperty(value = "商户退款单号")
    @JsonProperty("partner_refund_id")
    private String partnerRefundId;
    
    @ApiModelProperty(value = "退款金额")
    @JsonProperty("amount")
    private Integer amount;
    
    @ApiModelProperty(value = "币种")
    @JsonProperty("currency")
    private String currency;
    
    // 兼容性字段
    @ApiModelProperty(value = "响应码（兼容）")
    private String code;
    
    @ApiModelProperty(value = "响应消息（兼容）")
    private String message;
    
    @ApiModelProperty(value = "是否成功（兼容）")
    private Boolean success;
    
    @ApiModelProperty(value = "商户退款单号（兼容）")
    private String merRefundId;
    
    @ApiModelProperty(value = "退款状态（兼容）")
    private String refundStatus;
    
    @ApiModelProperty(value = "退款金额（分）（兼容）")
    private Long refundAmount;
    
    @ApiModelProperty(value = "退款时间（兼容）")
    private String refundTime;
    
    @ApiModelProperty(value = "退款完成时间（兼容）")
    private String refundCompleteTime;

    // Getters and Setters - 新API字段
    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getPartnerRefundId() {
        return partnerRefundId;
    }

    public void setPartnerRefundId(String partnerRefundId) {
        this.partnerRefundId = partnerRefundId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    // 兼容性字段的 Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMerRefundId() {
        return merRefundId;
    }

    public void setMerRefundId(String merRefundId) {
        this.merRefundId = merRefundId;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Long getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Long refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }

    public String getRefundCompleteTime() {
        return refundCompleteTime;
    }

    public void setRefundCompleteTime(String refundCompleteTime) {
        this.refundCompleteTime = refundCompleteTime;
    }

    @Override
    public String toString() {
        return "GlobePayRefundQueryResponse{" +
                "returnCode='" + returnCode + '\'' +
                ", returnMsg='" + returnMsg + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", refundId='" + refundId + '\'' +
                ", partnerRefundId='" + partnerRefundId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}