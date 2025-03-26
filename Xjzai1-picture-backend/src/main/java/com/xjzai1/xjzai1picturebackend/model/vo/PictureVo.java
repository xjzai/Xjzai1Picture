package com.xjzai1.xjzai1picturebackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.xjzai1.xjzai1picturebackend.model.domain.Picture;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PictureVo implements Serializable {
    /**
     * id
     */
    private Long id;

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
     * 简介
     */
    private String introduction;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 分类
     */
    private String category;

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
     * 用户 id
     */
    private Long userId;

    /**
     * 空间 id
     */
    private Long spaceId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVo user;

    private static final long serialVersionUID = 1L;

    /**
     * 封装类转对象
     */
    public static Picture voToObj(PictureVo pictureVo) {
        if (pictureVo == null) {
            return null;
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureVo, picture);
        // 类型不同，需要转换
        picture.setTags(JSONUtil.toJsonStr(pictureVo.getTags()));
        return picture;
    }

    /**
     * 对象转封装类
     */
    public static PictureVo objToVo(Picture picture) {
        if (picture == null) {
            return null;
        }
        PictureVo pictureVo = new PictureVo();
        BeanUtils.copyProperties(picture, pictureVo);
        // 类型不同，需要转换
        pictureVo.setTags(JSONUtil.toList(picture.getTags(), String.class));
        return pictureVo;
    }

}
