package com.xjzai1.xjzai1picturebackend.exception;

public enum ErrorCode {

    SUCCESS(0, "success", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求参数为空", ""),
    NO_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "已登录无权限", ""),
    FORBIDDEN_ERROR(40300, "禁止访问", ""),
    NOT_FOUND_ERROR(40400, "请求数据不存在", ""),
    SYSTEM_ERROR(50000, "系统内异常", ""),
    OPERATION_ERROR(50001, "操作失败", "");



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
