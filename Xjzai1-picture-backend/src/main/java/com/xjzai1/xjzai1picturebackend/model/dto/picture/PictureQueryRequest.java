package com.xjzai1.xjzai1picturebackend.model.dto.picture;

import com.xjzai1.xjzai1picturebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable{
    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 空间 id
     */
    private Long spaceId;

    /**
     * 是否只查询 spaceId 为 null 的数据
     */
    private Boolean nullSpaceId;

    /**
     * 标签（JSON 数组）
     */
    private List<String> tags;

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

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人 id
     */
    private Long reviewerId;


    private static final long serialVersionUID = 1L;
}
