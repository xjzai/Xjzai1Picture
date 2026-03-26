package com.xjzai1.xjzai1picturebackend.websocket.model;

import lombok.Getter;

@Getter
public enum PictureEditMessageTypeEnum {

    INFO("Notification", "INFO"),
    ERROR("Error", "ERROR"),
    ENTER_EDIT("Enter Edit Mode", "ENTER_EDIT"),
    EXIT_EDIT("Exit Edit Mode", "EXIT_EDIT"),
    EDIT_ACTION("Edit Action", "EDIT_ACTION");

    private final String text;
    private final String value;

    PictureEditMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static PictureEditMessageTypeEnum getEnumByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PictureEditMessageTypeEnum typeEnum : PictureEditMessageTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
