package com.xjzai1.xjzai1picturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum SpaceLevelEnum {

    COMMON("普通版", 0, 100, 100L * 1024 * 1024),
    PROFESSIONAL("专业版", 1, 1000, 1000L * 1024 * 1024),
    FLAGSHIP("旗舰版", 2, 10000, 10000L * 1024 * 1024);


    private final String text;
    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private final int value;

    /**
     * 空间图片的最大数量
     */
    private final long maxCount;

    /**
     * 空间图片的最大总大小
     */
    private final long maxSize;

    /**
     * 
     * @param text
     * @param value
     * @param maxCount
     * @param maxSize
     */
    SpaceLevelEnum(String text, int value, long maxCount, long maxSize) {
        this.text = text;
        this.value = value;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static SpaceLevelEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceLevelEnum spaceLevelEnum : SpaceLevelEnum.values()) {
            if (spaceLevelEnum.value == value) {
                return spaceLevelEnum;
            }
        }
        return null;
    }



}
