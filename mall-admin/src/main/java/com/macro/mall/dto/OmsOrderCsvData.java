package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单CSV导出数据
 * Created on 2025/7/3.
 */
@Data
public class OmsOrderCsvData {
    
    @ApiModelProperty("订单唯一标识(UUID格式)")
    private String orderId;
    
    @ApiModelProperty("用户唯一标识(UUID格式)")
    private String userId;
    
    @ApiModelProperty("下单时间")
    private Date orderDate;
    
    @ApiModelProperty("订单总金额")
    private BigDecimal totalAmount;
    
    @ApiModelProperty("订单状态")
    private String status;
    
    @ApiModelProperty("支付方式")
    private String paymentMethod;
    
    @ApiModelProperty("订单中商品总数")
    private Integer itemCount;
    
    @ApiModelProperty("货币代码")
    private String currency;
    
    @ApiModelProperty("收货地址")
    private String shippingAddress;
    
    @ApiModelProperty("收件人姓名")
    private String recipientName;
    
    @ApiModelProperty("收件人电话")
    private String recipientPhone;
}
