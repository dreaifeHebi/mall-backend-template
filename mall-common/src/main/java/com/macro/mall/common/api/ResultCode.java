package com.macro.mall.common.api;

/**
 * API返回码封装类
 * Created by macro on 2019/4/19.
 */
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "Success"),
    FAILED(500, "Operation failed"),
    VALIDATE_FAILED(404, "Invalid request parameters"),
    UNAUTHORIZED(401, "Not signed in or token expired"),
    FORBIDDEN(403, "No permission for this action");
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
