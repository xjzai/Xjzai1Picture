package com.xjzai1.xjzai1picturebackend.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.xjzai1.xjzai1picturebackend.model.domain.Space;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data
public class SpaceVo implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    /**
     * 当前空间下图片的总大小
     */
    private Long totalSize;

    /**
     * 当前空间下的图片数量
     */
    private Long totalCount;

    /**
     * 创建用户 id
     */
    private Long userId;

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
    private UserVo userVo;

    private static final long serialVersionUID = 1L;

    /**
     * 封装类转对象
     * @param spaceVo
     * @return
     */
    public static Space voToObj(SpaceVo spaceVo) {
        if (spaceVo == null) {
            return null;
        }
        Space space = new Space();
        BeanUtils.copyProperties(spaceVo, space);
        return space;
    }

    /**
     * 对象转封装类
     * @param space
     * @return
     */
    public static SpaceVo objToVo(Space space) {
        if (space == null) {
            return null;
        }
        SpaceVo spaceVo = new SpaceVo();
        BeanUtils.copyProperties(space, spaceVo);
        return spaceVo;
    }
}
