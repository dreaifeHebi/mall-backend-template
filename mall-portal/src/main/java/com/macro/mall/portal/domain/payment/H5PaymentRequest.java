package com.macro.mall.portal.domain.payment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * H5支付请求参数
 * @author macrozheng
 * @date 2025/7/26
 */
@ApiModel("H5支付请求参数")
public class H5PaymentRequest {

    @ApiModelProperty("订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @ApiModelProperty("订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderSn;

    @ApiModelProperty("支付金额")
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0.01")
    private BigDecimal totalAmount;

    @ApiModelProperty("货币代码，默认JPY")
    private String currency = "JPY";

    @ApiModelProperty("支付渠道：LOCAL_SELF、STRIPE_CHECKOUT、ALIPAY_H5、WECHAT_H5、CREDIT_CARD")
    @NotBlank(message = "支付渠道不能为空")
    private String paymentChannel;

    @ApiModelProperty("订单标题")
    @NotBlank(message = "订单标题不能为空")
    private String subject;

    @ApiModelProperty("信用卡信息（信用卡支付时必填）")
    private CreditCardInfo creditCardInfo;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public CreditCardInfo getCreditCardInfo() {
        return creditCardInfo;
    }

    public void setCreditCardInfo(CreditCardInfo creditCardInfo) {
        this.creditCardInfo = creditCardInfo;
    }

    /**
     * 信用卡信息
     */
    public static class CreditCardInfo {
        @ApiModelProperty("持卡人姓名")
        private String cardHolderName;

        @ApiModelProperty("卡号")
        private String cardNumber;

        @ApiModelProperty("有效期月份")
        private String expiryMonth;

        @ApiModelProperty("有效期年份")
        private String expiryYear;

        @ApiModelProperty("CVV")
        private String cvv;

        @ApiModelProperty("账单地址")
        private String billingAddress;

        public String getCardHolderName() {
            return cardHolderName;
        }

        public void setCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getExpiryMonth() {
            return expiryMonth;
        }

        public void setExpiryMonth(String expiryMonth) {
            this.expiryMonth = expiryMonth;
        }

        public String getExpiryYear() {
            return expiryYear;
        }

        public void setExpiryYear(String expiryYear) {
            this.expiryYear = expiryYear;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }

        public String getBillingAddress() {
            return billingAddress;
        }

        public void setBillingAddress(String billingAddress) {
            this.billingAddress = billingAddress;
        }
    }
}
