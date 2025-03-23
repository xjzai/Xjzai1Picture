package com.xjzai1.xjzai1picturebackend.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import com.xjzai1.xjzai1picturebackend.config.CosClientConfig;
import com.xjzai1.xjzai1picturebackend.exception.BusinessException;
import com.xjzai1.xjzai1picturebackend.exception.ErrorCode;
import com.xjzai1.xjzai1picturebackend.manager.CosManager;
import com.xjzai1.xjzai1picturebackend.model.dto.picture.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class PictureUploadTemplate {
    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 模板方法，定义上传流程
     */
    public final UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 1. 校验图片
        validPicture(inputSource);

        // 2. 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originFilename = getOriginFilename(inputSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        try {
            // 3. 创建临时文件
            file = File.createTempFile(uploadPath, null);
            // 处理文件来源
            processFile(inputSource, file);

            // 4. 上传图片
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo(); //

            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                CIObject compressedCiObject = objectList.get(0);
                // 缩略图默认等于压缩图
                CIObject thumbnailCiObject = compressedCiObject;
                // 有生成缩略图，才得到缩略图
                if (objectList.size() > 1) {
                    thumbnailCiObject = objectList.get(1);
                }
                // 封装压缩图返回结果
                return buildResult(originFilename, uploadPath, compressedCiObject, thumbnailCiObject);
            }

            // 5. 封装返回结果
            return buildResult(originFilename, file, uploadPath, imageInfo);
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }

    private UploadPictureResult buildResult(String originFilename, String uploadPath, CIObject compressedCiObject, CIObject thumbnailCiObject) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPictureWidth(picWidth);
        uploadPictureResult.setPictureHeight(picHeight);
        uploadPictureResult.setPictureScale(picScale);
        uploadPictureResult.setPictureFormat(compressedCiObject.getFormat());
        uploadPictureResult.setPictureSize(compressedCiObject.getSize().longValue());
        // 设置图片原图地址
        uploadPictureResult.setOriginalUrl(cosClientConfig.getHost() + "/" + uploadPath);
        // 设置图片为压缩后的地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
        // 设置缩略图
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        return uploadPictureResult;
    }

    /**
     * 封装返回结果
     *
     * @param originFilename
     * @param file
     * @param uploadPath
     * @param imageInfo      对象存储返回的信息
     * @return
     */
    private UploadPictureResult buildResult(String originFilename, File file, String uploadPath, ImageInfo imageInfo) {
        // 封装返回结果
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int pictureWidth = imageInfo.getWidth();
        int pictureHeight = imageInfo.getHeight();
        double pictureScale = NumberUtil.round(pictureWidth * 1.0 / pictureHeight, 2).doubleValue();
        uploadPictureResult.setName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPictureWidth(pictureWidth);
        uploadPictureResult.setPictureHeight(pictureHeight);
        uploadPictureResult.setPictureScale(pictureScale);
        uploadPictureResult.setPictureFormat(imageInfo.getFormat());
        uploadPictureResult.setPictureSize(FileUtil.size(file));
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        return uploadPictureResult;
    }

    /**
     * 校验输入源（本地文件或 URL）
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取输入源的原始文件名
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 处理输入源并生成本地临时文件
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;


    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

}
