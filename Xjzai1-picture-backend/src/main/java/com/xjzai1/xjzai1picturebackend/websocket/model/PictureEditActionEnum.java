package com.xjzai1.xjzai1picturebackend.websocket.model;

import lombok.Getter;

@Getter
public enum PictureEditActionEnum {

    ZOOM_IN("Zoom In", "ZOOM_IN"),
    ZOOM_OUT("Zoom Out", "ZOOM_OUT"),
    ROTATE_LEFT("Rotate Left", "ROTATE_LEFT"),
    ROTATE_RIGHT("Rotate Right", "ROTATE_RIGHT");

    private final String text;
    private final String value;

    PictureEditActionEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static PictureEditActionEnum getEnumByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (PictureEditActionEnum actionEnum : PictureEditActionEnum.values()) {
            if (actionEnum.value.equals(value)) {
                return actionEnum;
            }
        }
        return null;
    }
}
