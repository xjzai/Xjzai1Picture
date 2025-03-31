package com.xjzai1.xjzai1picturebackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片标签分类列表试图
 */
@Data
public class PictureTagCategory implements Serializable {

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */
    private List<String> categoryList;

    private static final long serialVersionUID = 1L;
}
