package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 客服微信信息DTO
 * Created by mall on 2025/06/22.
 */
public class CustomerServiceDto {
    @ApiModelProperty(value = "客服微信号")
    private String wechatId;

    @ApiModelProperty(value = "客服姓名")
    private String customerServiceName;

    @ApiModelProperty(value = "微信二维码URL")
    private String qrCodeUrl;

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getCustomerServiceName() {
        return customerServiceName;
    }

    public void setCustomerServiceName(String customerServiceName) {
        this.customerServiceName = customerServiceName;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
