package com.xjzai1.xjzai1picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjzai1.xjzai1picturebackend.model.domain.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjzai1.xjzai1picturebackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.xjzai1.xjzai1picturebackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.xjzai1.xjzai1picturebackend.model.vo.SpaceUserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Administrator
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-04-16 21:50:37
*/
public interface SpaceUserService extends IService<SpaceUser> {

    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    SpaceUserVo getSpaceUserVo(SpaceUser spaceUser, HttpServletRequest request);

    List<SpaceUserVo> getSpaceUserVoList(List<SpaceUser> spaceUserList);

    void validSpaceUser(SpaceUser spaceUser, boolean add);

    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);
}
