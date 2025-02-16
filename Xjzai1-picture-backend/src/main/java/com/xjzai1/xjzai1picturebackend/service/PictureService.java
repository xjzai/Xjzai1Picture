package com.xjzai1.xjzai1picturebackend.service;

import com.xjzai1.xjzai1picturebackend.model.domain.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjzai1.xjzai1picturebackend.model.domain.User;
import com.xjzai1.xjzai1picturebackend.model.dto.picture.PictureUploadRequest;
import com.xjzai1.xjzai1picturebackend.model.vo.PictureVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Administrator
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-02-15 17:03:23
 */
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVo uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

}
