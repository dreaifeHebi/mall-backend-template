package com.macro.mall.portal.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * GlobePay信用卡支付响应对象
 */
@Data
public class GlobePayCreditCardResponse {
    
    @JsonProperty("return_code")
    private String returnCode;
    
    @JsonProperty("return_msg")
    private String returnMsg;
    
    @JsonProperty("result_code")
    private String resultCode;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("partner_code")
    private String partnerCode;
    
    @JsonProperty("order_id")
    private String orderId;
    
    @JsonProperty("partner_order_id")
    private String partnerOrderId;
    
    @JsonProperty("pay_url")
    private String payUrl;
    
    @JsonProperty("bindcard_url")
    private String bindcardUrl;
    
    @JsonProperty("member_token")
    private String memberToken;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("trade_no")
    private String tradeNo;
    
    @JsonProperty("partner_trade_no")
    private String partnerTradeNo;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("nonce_str")
    private String nonceStr;
    
    @JsonProperty("sign")
    private String sign;
}