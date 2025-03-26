package com.xjzai1.xjzai1picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjzai1.xjzai1picturebackend.common.DeleteRequest;
import com.xjzai1.xjzai1picturebackend.model.domain.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjzai1.xjzai1picturebackend.model.domain.User;
import com.xjzai1.xjzai1picturebackend.model.dto.picture.*;
import com.xjzai1.xjzai1picturebackend.model.vo.PictureVo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-02-15 17:03:23
 */
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVo uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );


    /**
     * 获取QueryWrapper
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取vo类
     * @param picture
     * @param request
     * @return
     */
    PictureVo getPictureVo(Picture picture, HttpServletRequest request);

    Page<PictureVo> getPictureVoPage(Page<Picture> picturePage, HttpServletRequest request);

    boolean deletePicture(Long pictureId, User loginUser);

    boolean editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    void validPicture(Picture picture);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    void fillReviewParams(Picture picture, User loginUser);

    void checkPictureAuth(User loginUser, Picture picture);

    @Async
    void clearPictureFile(Picture oldPicture);
}
