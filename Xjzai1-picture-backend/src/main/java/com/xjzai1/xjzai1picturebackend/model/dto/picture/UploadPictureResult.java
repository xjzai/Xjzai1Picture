package com.xjzai1.xjzai1picturebackend.model.dto.picture;

import lombok.Data;

@Data
public class UploadPictureResult {

    /**
     * 图片 url
     */
    private String url;

    /**
     * 缩略图 Url
     */
    private String thumbnailUrl;

    /**
     * 原图 Url
     */
    private String originalUrl;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 图片体积
     */
    private Long pictureSize;

    /**
     * 图片宽度
     */
    private Integer pictureWidth;

    /**
     * 图片高度
     */
    private Integer pictureHeight;

    /**
     * 图片宽高比例
     */
    private Double pictureScale;

    /**
     * 图片格式
     */
    private String pictureFormat;

}

