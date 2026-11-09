package com.macro.mall.portal.domain.payment;

import lombok.Data;

/**
 * GlobePay信用卡支付请求参数
 * @author dreaifekks
 * @date 2025/7/26
 */
@Data
public class GlobePayCreditCardRequest {

    /**
     * 订单标题（最大长度128字符）
     */
    private String description;

    /**
     * 绑卡获得的member_token，用于token支付
     */
    private String memberToken;

    /**
     * 金额，单位日元
     */
    private Integer price;

    /**
     * 预授权标记，默认false
     */
    private Boolean preauth = false;

    /**
     * 超时时间，默认5m
     */
    private String expire = "5m";

    /**
     * 支付通知url
     */
    private String notifyUrl;

    /**
     * 操作人员标识
     */
    private String operator;

    /**
     * 订单扩展参数
     */
    private Extra extra;

    /**
     * 消费者信息
     */
    private Customer customer;

    @Data
    public static class Extra {
        /**
         * 3DS2.0安全模式类型
         */
        private String tdsType;
    }

    @Data
    public static class Customer {
        /**
         * 消费者姓名
         */
        private String name;

        /**
         * 邮编
         */
        private String postcode;

        /**
         * 地址
         */
        private String address;

        /**
         * 城市
         */
        private String city;

        /**
         * 州、省
         */
        private String state;

        /**
         * 国家
         */
        private String country;
    }
}
