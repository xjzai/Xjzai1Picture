package com.xjzai1.xjzai1picturebackend.common;

import com.xjzai1.xjzai1picturebackend.exception.ErrorCode;

public class ResultUtils {
    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<T>(0, data, "success");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     * @param
     */
    public static BaseResponse error (ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error (int code, String message, String description){
        return new BaseResponse<>(code, null, message, description);
    }

    /**
     * 失败
     * @param errorCode
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error (ErrorCode errorCode, String message, String description){
        return new BaseResponse<>(errorCode.getCode(),null, message, description);
    }

    /**
     * 失败
     * @param errorCode
     * @param description
     * @return
     */
    public static BaseResponse error (ErrorCode errorCode, String description){
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), description);
    }
}
