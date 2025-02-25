package com.xjzai1.xjzai1picturebackend.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjzai1.xjzai1picturebackend.annotation.AuthCheck;
import com.xjzai1.xjzai1picturebackend.common.BaseResponse;
import com.xjzai1.xjzai1picturebackend.common.DeleteRequest;
import com.xjzai1.xjzai1picturebackend.common.PageRequest;
import com.xjzai1.xjzai1picturebackend.common.ResultUtils;
import com.xjzai1.xjzai1picturebackend.constant.UserConstant;
import com.xjzai1.xjzai1picturebackend.exception.BusinessException;
import com.xjzai1.xjzai1picturebackend.exception.ErrorCode;
import com.xjzai1.xjzai1picturebackend.exception.ThrowUtils;
import com.xjzai1.xjzai1picturebackend.model.domain.Picture;
import com.xjzai1.xjzai1picturebackend.model.domain.User;
import com.xjzai1.xjzai1picturebackend.model.dto.picture.PictureEditRequest;
import com.xjzai1.xjzai1picturebackend.model.dto.picture.PictureQueryRequest;
import com.xjzai1.xjzai1picturebackend.model.dto.picture.PictureUpdateRequest;
import com.xjzai1.xjzai1picturebackend.model.dto.picture.PictureUploadRequest;
import com.xjzai1.xjzai1picturebackend.model.vo.PictureTagCategory;
import com.xjzai1.xjzai1picturebackend.model.vo.PictureVo;
import com.xjzai1.xjzai1picturebackend.service.PictureService;
import com.xjzai1.xjzai1picturebackend.service.UserService;
import javafx.geometry.Pos;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private PictureService pictureService;

    @Resource
    private UserService userService;

    /**
     * 上传图片（可重新上传）
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVo> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PictureVo pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 删除图片
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Picture oldPicture = pictureService.getById(id);
        if (oldPicture == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或者管理员可以删除
        if (!userService.isAdmin(loginUser) || !Objects.equals(oldPicture.getUserId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 操作数据库
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "数据库删除失败");
        return ResultUtils.success(true);
    }

    /**
     * 更新图片（仅管理员可用）
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest, HttpServletRequest request) {
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 校验参数
        pictureService.validPicture(picture);
        // 判断是否存在
        User loginUser = userService.getLoginUser(request);
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "数据库更新失败");
        return ResultUtils.success(true);

    }

    /**
     * 根据 id 获取图片（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR, "未找到该数据");
        return ResultUtils.success(picture);
    }

    /**
     * 根据 id 获取图片（封装类）
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVo> getPictureVoById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(pictureService.getPictureVo(picture, request));
    }

    /**
     * 分页获取图片列表（仅管理员可用）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();

        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    /**
     * 分页获取图片列表（封装类）
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVo>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                             HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        return ResultUtils.success(pictureService.getPictureVoPage(picturePage, request));
    }

    /**
     * 编辑图片（给用户使用）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        pictureService.validPicture(picture);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 获取预置标签和分类
     * @return
     */
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }



}
