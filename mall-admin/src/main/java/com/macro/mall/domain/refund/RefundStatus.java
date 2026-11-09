package com.macro.mall.domain.refund;

/**
 * 退款状态枚举
 * @author dreaifekks
 * @date 2025/10/13
 */
public enum RefundStatus {

    /** 待审核 */
    PENDING_AUDIT("PENDING_AUDIT", "待审核"),

    /** 审核通过 */
    APPROVED("APPROVED", "审核通过"),

    /** 审核拒绝 */
    REJECTED("REJECTED", "审核拒绝"),

    /** 退款处理中 */
    PROCESSING("PROCESSING", "退款处理中"),

    /** 退款成功 */
    SUCCESS("SUCCESS", "退款成功"),

    /** 退款失败 */
    FAILED("FAILED", "退款失败"),

    /** 已取消 */
    CANCELLED("CANCELLED", "已取消"),

    /** 部分退款成功 */
    PARTIAL_SUCCESS("PARTIAL_SUCCESS", "部分退款成功");

    private final String code;
    private final String description;

    RefundStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码获取枚举
     */
    public static RefundStatus fromCode(String code) {
        for (RefundStatus status : RefundStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid refund status code: " + code);
    }

    /**
     * 检查是否可以进行审核
     */
    public boolean canAudit() {
        return this == PENDING_AUDIT;
    }

    /**
     * 检查是否可以取消
     */
    public boolean canCancel() {
        return this == PENDING_AUDIT || this == APPROVED;
    }

    /**
     * 检查是否可以重新处理
     */
    public boolean canReprocess() {
        return this == FAILED;
    }

    /**
     * 检查是否是最终状态
     */
    public boolean isFinalStatus() {
        return this == SUCCESS || this == REJECTED || this == CANCELLED || this == PARTIAL_SUCCESS;
    }

    /**
     * 检查是否是成功状态
     */
    public boolean isSuccessStatus() {
        return this == SUCCESS || this == PARTIAL_SUCCESS;
    }

    /**
     * 检查是否需要查询第三方状态
     */
    public boolean needQueryThirdParty() {
        return this == PROCESSING;
    }
}
