package com.xjzai1.xjzai1picturebackend.exception;

public enum ErrorCode {

    SUCCESS(0, "success", ""),
    PARAMS_ERROR(40000, "Invalid request parameters", ""),
    NULL_ERROR(40001, "Request parameters are empty", ""),
    NO_LOGIN(40100, "Not logged in", ""),
    NO_AUTH(40101, "Logged in but no permission", ""),
    FORBIDDEN_ERROR(40300, "Forbidden access", ""),
    NOT_FOUND_ERROR(40400, "Requested data not found", ""),
    SYSTEM_ERROR(50000, "System error", ""),
    OPERATION_ERROR(50001, "Operation failed", "");



    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 状态码描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
