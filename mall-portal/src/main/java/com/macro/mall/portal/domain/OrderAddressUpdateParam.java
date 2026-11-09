package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单地址更新参数
 * Created by macro on 2025/7/14.
 */
@Data
public class OrderAddressUpdateParam {
    
    @ApiModelProperty("订单ID")
    private String orderId;
    
    @ApiModelProperty("新地址信息")
    private AddressInfo newAddress;
    
    @Data
    public static class AddressInfo {
        @ApiModelProperty("收货人姓名")
        private String receiverName;
        
        @ApiModelProperty("收货人电话")
        private String receiverPhone;
        
        @ApiModelProperty("省份")
        private String receiverProvince;
        
        @ApiModelProperty("城市")
        private String receiverCity;
        
        @ApiModelProperty("地区")
        private String receiverRegion;
        
        @ApiModelProperty("详细地址")
        private String receiverDetailAddress;
        
        @ApiModelProperty("邮编")
        private String receiverPostCode;
    }
}
