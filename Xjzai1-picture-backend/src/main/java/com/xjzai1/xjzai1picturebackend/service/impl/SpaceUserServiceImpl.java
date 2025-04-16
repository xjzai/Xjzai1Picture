package com.xjzai1.xjzai1picturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjzai1.xjzai1picturebackend.exception.BusinessException;
import com.xjzai1.xjzai1picturebackend.exception.ErrorCode;
import com.xjzai1.xjzai1picturebackend.exception.ThrowUtils;
import com.xjzai1.xjzai1picturebackend.model.domain.Space;
import com.xjzai1.xjzai1picturebackend.model.domain.SpaceUser;
import com.xjzai1.xjzai1picturebackend.model.domain.User;
import com.xjzai1.xjzai1picturebackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.xjzai1.xjzai1picturebackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.xjzai1.xjzai1picturebackend.model.enums.SpaceRoleEnum;
import com.xjzai1.xjzai1picturebackend.model.vo.SpaceUserVo;
import com.xjzai1.xjzai1picturebackend.model.vo.SpaceVo;
import com.xjzai1.xjzai1picturebackend.model.vo.UserVo;
import com.xjzai1.xjzai1picturebackend.service.SpaceService;
import com.xjzai1.xjzai1picturebackend.service.SpaceUserService;
import com.xjzai1.xjzai1picturebackend.mapper.SpaceUserMapper;
import com.xjzai1.xjzai1picturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
* @createDate 2025-04-16 21:50:37
*/
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
    implements SpaceUserService{

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Override
    public long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        // 参数校验
        ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
        validSpaceUser(spaceUser, true);
        // 数据库操作
        boolean result = this.save(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
    }

    @Override
    public SpaceUserVo getSpaceUserVo(SpaceUser spaceUser, HttpServletRequest request) {
        // 对象转封装类
        SpaceUserVo spaceUserVo = SpaceUserVo.objToVo(spaceUser);
        // 关联查询用户信息
        Long userId = spaceUser.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVo userVo = userService.getUserVo(user);
            spaceUserVo.setUser(userVo);
        }
        // 关联查询空间信息
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceService.getById(spaceId);
            SpaceVo spaceVo = spaceService.getSpaceVo(space, request);
            spaceUserVo.setSpace(spaceVo);
        }
        return spaceUserVo;
    }

    @Override
    public List<SpaceUserVo> getSpaceUserVoList(List<SpaceUser> spaceUserList) {
        // 判断输入列表是否为空
        if (CollUtil.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }
        // 对象列表 => 封装对象列表
        List<SpaceUserVo> spaceUserVoList = spaceUserList.stream().map(SpaceUserVo::objToVo).collect(Collectors.toList());
        // 1. 收集需要关联查询的用户 ID 和空间 ID
        Set<Long> userIdSet = spaceUserList.stream().map(SpaceUser::getUserId).collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUserList.stream().map(SpaceUser::getSpaceId).collect(Collectors.toSet());
        // 2. 批量查询用户和空间
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdSpaceListMap = spaceService.listByIds(spaceIdSet).stream()
                .collect(Collectors.groupingBy(Space::getId));
        // 3. 填充 SpaceUserVo 的用户和空间信息
        spaceUserVoList.forEach(spaceUserVo -> {
            Long userId = spaceUserVo.getUserId();
            Long spaceId = spaceUserVo.getSpaceId();
            // 填充用户信息
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceUserVo.setUser(userService.getUserVo(user));
            // 填充空间信息
            Space space = null;
            if (spaceIdSpaceListMap.containsKey(spaceId)) {
                space = spaceIdSpaceListMap.get(spaceId).get(0);
            }
            spaceUserVo.setSpace(SpaceVo.objToVo(space));
        });
        return spaceUserVoList;
    }




    @Override
    public void validSpaceUser(SpaceUser spaceUser, boolean add) {
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.PARAMS_ERROR);
        // 创建时，空间 id 和用户 id 必填
        Long spaceId = spaceUser.getSpaceId();
        Long userId = spaceUser.getUserId();
        if (add) {
            ThrowUtils.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
            User user = userService.getById(userId);
            ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        }
        // 校验空间角色
        String spaceRole = spaceUser.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        if (spaceRole != null && spaceRoleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间角色不存在");
        }
    }

    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "space_id", spaceId);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "space_role", spaceRole);
        return queryWrapper;
    }



}




