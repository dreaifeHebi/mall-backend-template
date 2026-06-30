package com.macro.mall.domain.refund;

/**
 * 退款操作类型枚举
 * @author dreaifekks
 * @date 2025/10/13
 */
public enum RefundOperationType {

    /** 申请退款 */
    APPLY("APPLY", "申请退款"),

    /** 审核通过 */
    AUDIT_APPROVED("AUDIT_APPROVED", "审核通过"),

    /** 审核拒绝 */
    AUDIT_REJECTED("AUDIT_REJECTED", "审核拒绝"),

    /** 发起退款 */
    INITIATE_REFUND("INITIATE_REFUND", "发起退款"),

    /** 查询退款状态 */
    QUERY_STATUS("QUERY_STATUS", "查询退款状态"),

    /** 退款成功 */
    REFUND_SUCCESS("REFUND_SUCCESS", "退款成功"),

    /** 退款失败 */
    REFUND_FAILED("REFUND_FAILED", "退款失败"),

    /** 取消退款 */
    CANCEL("CANCEL", "取消退款"),

    /** 系统自动查询 */
    AUTO_QUERY("AUTO_QUERY", "系统自动查询"),

    /** 手动重试 */
    MANUAL_RETRY("MANUAL_RETRY", "手动重试"),

    /** 管理员强制完成 */
    ADMIN_FORCE_COMPLETE("ADMIN_FORCE_COMPLETE", "管理员强制完成"),

    /** 管理员强制失败 */
    ADMIN_FORCE_FAIL("ADMIN_FORCE_FAIL", "管理员强制失败"),

    /** 批量处理 */
    BATCH_PROCESS("BATCH_PROCESS", "批量处理");

    private final String code;
    private final String description;

    RefundOperationType(String code, String description) {
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
     * 根据操作码获取枚举
     */
    public static RefundOperationType fromCode(String code) {
        for (RefundOperationType type : RefundOperationType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid operation type code: " + code);
    }

    /**
     * 检查是否是系统自动操作
     */
    public boolean isSystemOperation() {
        return this == AUTO_QUERY || this == BATCH_PROCESS;
    }

    /**
     * 检查是否是管理员操作
     */
    public boolean isAdminOperation() {
        return this == AUDIT_APPROVED || this == AUDIT_REJECTED ||
               this == ADMIN_FORCE_COMPLETE || this == ADMIN_FORCE_FAIL ||
               this == MANUAL_RETRY;
    }
}
