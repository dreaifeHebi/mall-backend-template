package com.macro.mall.portal.domain.payment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付方式实体类
 * @author macrozheng
 * @date 2025/7/26
 */
@ApiModel("支付方式")
public class PaymentMethod {

    @ApiModelProperty("支付方式ID")
    private Long id;

    @ApiModelProperty("支付方式代码")
    private String methodCode;

    @ApiModelProperty("支付方式名称")
    private String methodName;

    @ApiModelProperty("支付渠道")
    private String channel;

    @ApiModelProperty("支付类型")
    private String type;

    @ApiModelProperty("图标URL")
    private String iconUrl;

    @ApiModelProperty("状态：1-启用，0-禁用")
    private Integer status;

    @ApiModelProperty("最小金额")
    private BigDecimal minAmount;

    @ApiModelProperty("最大金额")
    private BigDecimal maxAmount;

    @ApiModelProperty("手续费率")
    private BigDecimal feeRate;

    @ApiModelProperty("固定手续费")
    private BigDecimal fixedFee;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("支持的货币")
    private String supportedCurrency;

    @ApiModelProperty("配置信息")
    private String config;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    public BigDecimal getFixedFee() {
        return fixedFee;
    }

    public void setFixedFee(BigDecimal fixedFee) {
        this.fixedFee = fixedFee;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getSupportedCurrency() {
        return supportedCurrency;
    }

    public void setSupportedCurrency(String supportedCurrency) {
        this.supportedCurrency = supportedCurrency;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
