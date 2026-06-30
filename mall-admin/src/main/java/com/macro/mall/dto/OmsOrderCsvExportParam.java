package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 订单CSV导出参数
 * Created on 2025/7/3.
 */
@Data
public class OmsOrderCsvExportParam {
    
    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    
    @ApiModelProperty("订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单")
    private Integer status;
    
    @ApiModelProperty("支付方式：0->未支付；1->支付宝；2->微信")
    private Integer payType;
    
    @ApiModelProperty("订单来源：0->PC订单；1->app订单")
    private Integer sourceType;
    
    @ApiModelProperty("指定订单ID列表")
    private List<Long> orderIds;
}
