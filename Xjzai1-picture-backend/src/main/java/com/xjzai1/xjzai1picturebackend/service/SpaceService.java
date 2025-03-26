package com.xjzai1.xjzai1picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjzai1.xjzai1picturebackend.model.domain.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjzai1.xjzai1picturebackend.model.domain.User;
import com.xjzai1.xjzai1picturebackend.model.dto.space.SpaceAddRequest;
import com.xjzai1.xjzai1picturebackend.model.dto.space.SpaceQueryRequest;
import com.xjzai1.xjzai1picturebackend.model.vo.SpaceVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-03-24 15:12:56
*/
public interface SpaceService extends IService<Space> {

    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    SpaceVo getSpaceVo(Space space, HttpServletRequest request);

    Page<SpaceVo> getSpaceVoPage(Page<Space> spacePage, HttpServletRequest request);

    void validSpace(Space space, Boolean add);

    void fillSpaceBySpaceLevel(Space space);
}
