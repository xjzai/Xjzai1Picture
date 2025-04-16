package com.xjzai1.xjzai1picturebackend.model.vo;

import com.xjzai1.xjzai1picturebackend.model.domain.SpaceUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data
public class SpaceUserVo implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间 id
     */
    private Long spaceId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户信息
     */
    private UserVo user;

    /**
     * 空间信息
     */
    private SpaceVo space;

    private static final long serialVersionUID = 1L;

    /**
     * 封装类转对象
     *
     * @param spaceUserVo
     * @return
     */
    public static SpaceUser voToObj(SpaceUserVo spaceUserVo) {
        if (spaceUserVo == null) {
            return null;
        }
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserVo, spaceUser);
        return spaceUser;
    }

    /**
     * 对象转封装类
     *
     * @param spaceUser
     * @return
     */
    public static SpaceUserVo objToVo(SpaceUser spaceUser) {
        if (spaceUser == null) {
            return null;
        }
        SpaceUserVo spaceUserVo = new SpaceUserVo();
        BeanUtils.copyProperties(spaceUser, spaceUserVo);
        return spaceUserVo;
    }
}

