package com.macro.mall.portal.service.refund;

/**
 * 退款状态枚举
 * @author dreaifekks
 * @date 2025/10/14
 */
public enum RefundStatus {
    PENDING_AUDIT("PENDING_AUDIT", "待审核"),
    APPROVED("APPROVED", "已同意"),
    REJECTED("REJECTED", "已拒绝"),
    PROCESSING("PROCESSING", "退款中"),
    SUCCESS("SUCCESS", "退款成功"),
    FAILED("FAILED", "退款失败"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String message;

    RefundStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static RefundStatus fromCode(String code) {
        for (RefundStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
