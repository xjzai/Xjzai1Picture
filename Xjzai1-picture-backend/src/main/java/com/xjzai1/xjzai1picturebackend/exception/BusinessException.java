package com.xjzai1.xjzai1picturebackend.exception;

import lombok.Getter;

/**
 * 自定义异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;
    private final String description;

    // todo decription有时候会返回数据库信息，不安全，后续看看怎么改
    public BusinessException(int code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

}
