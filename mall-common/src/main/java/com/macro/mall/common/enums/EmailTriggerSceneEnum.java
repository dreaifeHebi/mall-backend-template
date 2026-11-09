package com.macro.mall.common.enums;

/**
 * 邮件模板触发场景枚举
 * Created by mall on 2024/06/22.
 */
public enum EmailTriggerSceneEnum {
    ORDER_NOTIFICATION(0, "订单通知"),
    ORDER_TRACKING(1, "单号通知"),
    REGIST_CONFIRMATION(2, "注册确认"),
    ORDER_MODIFICATION(3, "修改订单"),
    USER_PASSWORD_RESET(4, "找回密码");

    private final Integer code;
    private final String message;

    EmailTriggerSceneEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessageByCode(Integer code) {
        for (EmailTriggerSceneEnum scene : values()) {
            if (scene.getCode().equals(code)) {
                return scene.getMessage();
            }
        }
        return "未知场景";
    }
}
