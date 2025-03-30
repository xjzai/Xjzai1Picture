package com.xjzai1.xjzai1picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjzai1.xjzai1picturebackend.model.domain.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjzai1.xjzai1picturebackend.model.domain.Space;
import com.xjzai1.xjzai1picturebackend.model.domain.User;
import com.xjzai1.xjzai1picturebackend.model.dto.picture.*;
import com.xjzai1.xjzai1picturebackend.model.vo.PictureVo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

//    Page<PictureVo> getPictureVoPage(Page<Picture> picturePage, HttpServletRequest request);

    Page<PictureVo> getPictureVoPage(Page<Picture> picturePage, HttpServletRequest request, String pictureColor);

    boolean deletePicture(Long pictureId, User loginUser);

    // todo 批量删除感觉有些问题，日后完善，桶内有残余未删除，可以先根据id从桶内查询全部图片再删除。
    boolean deletePictures(List<Picture> pictureList, User loginUser);


    @Transactional(rollbackFor = Exception.class)
    void doPictureDeleteByBatch(PictureDeleteByBatchRequest pictureDeleteByBatchRequest, User loginUser);

    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    @Transactional(rollbackFor = Exception.class)
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    void validPicture(Picture picture);

    List<Picture> orderPictureByColor(List<Picture> pictureList, String pictureColor);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    @Transactional(rollbackFor = Exception.class)
    void doPictureReviewByBatch(PictureReviewByBatchRequest pictureReviewByBatchRequest, User loginUser);

    void fillReviewParams(Picture picture, User loginUser);

    void checkPictureAuth(User loginUser, Picture picture);

    void checkSpaceAuth(User loginUser, Space space);

    @Async // 此处使用了异步注解，需要在启动类添加@EnableAsync注解才能生效
    void clearPictureFile(Picture oldPicture);

    // 先不使用异步注解，防止数据库删除了但COS没有
    void clearPictureFiles(List<Picture> pictureList);
}
