package com.xjzai1.xjzai1picturebackend.service.impl;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjzai1.xjzai1picturebackend.config.CosClientConfig;
import com.xjzai1.xjzai1picturebackend.exception.BusinessException;
import com.xjzai1.xjzai1picturebackend.exception.ErrorCode;
import com.xjzai1.xjzai1picturebackend.exception.ThrowUtils;
import com.xjzai1.xjzai1picturebackend.manager.CosManager;
import com.xjzai1.xjzai1picturebackend.manager.upload.FilePictureUpload;
import com.xjzai1.xjzai1picturebackend.manager.upload.PictureUploadTemplate;
import com.xjzai1.xjzai1picturebackend.manager.upload.UrlPictureUpload;
import com.xjzai1.xjzai1picturebackend.model.domain.Picture;
import com.xjzai1.xjzai1picturebackend.model.domain.Space;
import com.xjzai1.xjzai1picturebackend.model.domain.User;
import com.xjzai1.xjzai1picturebackend.model.dto.picture.*;
import com.xjzai1.xjzai1picturebackend.model.enums.PictureReviewStatusEnum;
import com.xjzai1.xjzai1picturebackend.model.vo.PictureVo;
import com.xjzai1.xjzai1picturebackend.model.vo.UserVo;
import com.xjzai1.xjzai1picturebackend.service.PictureService;
import com.xjzai1.xjzai1picturebackend.mapper.PictureMapper;
import com.xjzai1.xjzai1picturebackend.service.SpaceService;
import com.xjzai1.xjzai1picturebackend.service.UserService;
import com.xjzai1.xjzai1picturebackend.utils.ColorSimilarUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-02-15 17:03:23
 */
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private CosManager cosManager;

    @Autowired
    private CosClientConfig cosClientConfig;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public PictureVo uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH);
        // 校验空间是否存在
        Long spaceId = pictureUploadRequest.getSpaceId();
        if (spaceId != null && spaceId != 0L) {
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            // 必须空间创建人/管理人才能上传, 已经使用Sa-token进行全局鉴权
//            if (!loginUser.getId().equals(space.getUserId())) {
//                throw new BusinessException(ErrorCode.NO_AUTH, "没有空间权限");
//            }
            // 校验额度
            if (space.getTotalCount() >= space.getMaxCount()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间条数不足");
            }
            if (space.getTotalSize() >= space.getMaxSize()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间大小不足");
            }
        }
        // 补充空间 id，默认为 0
        if (spaceId == null) {
            spaceId = 0L;
        }
        // 用于判断是新增还是更新图片
        Long pictureId = pictureUploadRequest.getId();
        // 如果是更新图片，需要校验图片是否存在
        if (pictureId != null) {
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("space_id", spaceId)
                    .eq("id", pictureId);
            Picture oldPicture = this.getOne(queryWrapper);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            // 仅本人或者管理员可修改, 已经使用Sa-token进行全局鉴权
//            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//                throw new BusinessException(ErrorCode.NO_AUTH);
//            }
            // 校验空间是否一致
            // 没传 spaceId，则复用原有图片的 spaceId
            if (spaceId == null) {
                if (oldPicture.getSpaceId() != null) {
                    spaceId = oldPicture.getSpaceId();
                }
            } else {
                // 传了 spaceId，必须和原有图片一致
                if (ObjUtil.notEqual(spaceId, oldPicture.getSpaceId())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间 id 不一样");
                }
            }
            // 并且删除COS中的旧图片
            clearPictureFile(oldPicture);
        }
        // 上传图片，得到信息
        // 按照用户 id 划分目录
        String uploadPathPrefix;
        // todo 自己修改的地方，没bug可以删掉
//        if (spaceId == null) {
        if (spaceId == 0L) {
            uploadPathPrefix = String.format("public/%s", loginUser.getId());
        } else {
            uploadPathPrefix = String.format("space/%s", spaceId);
        }
        // 根据 inputSource 类型区分上传方式
        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        // 构造要入库的图片信息
        Picture picture = new Picture();
        // 补充设置 spaceId
        picture.setSpaceId(spaceId);
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
        picture.setOriginalUrl(uploadPictureResult.getOriginalUrl());
        // 图片名称特判，因为爬虫抓取那里可能会传入名称
        String pictureName = uploadPictureResult.getName();
        if (pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPictureName())) {
            pictureName = pictureUploadRequest.getPictureName();
        }
        picture.setName(pictureName);
        picture.setPictureSize(uploadPictureResult.getPictureSize());
        picture.setPictureWidth(uploadPictureResult.getPictureWidth());
        picture.setPictureHeight(uploadPictureResult.getPictureHeight());
        picture.setPictureScale(uploadPictureResult.getPictureScale());
        picture.setPictureFormat(uploadPictureResult.getPictureFormat());
        picture.setPictureColor(uploadPictureResult.getPictureColor());
        picture.setUserId(loginUser.getId());
        // 补充审核状态
        fillReviewParams(picture, loginUser);
        // 如果 pictureId 不为空，表示更新，否则是新增
        if (pictureId != null) {
            // 如果是更新，需要补充 id 和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        // 开启事务
        Long finalSpaceId = spaceId;
        transactionTemplate.execute(status -> {
//            boolean result = this.saveOrUpdate(picture);
            boolean result;
            if (pictureId != null) {
                UpdateWrapper<Picture> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("space_id", finalSpaceId)
                        .eq("id", pictureId);
                result = this.update(picture, updateWrapper);
            } else {
                result = this.save(picture);
            }
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
//            if (finalSpaceId != null) {
            if (finalSpaceId != 0L) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, finalSpaceId)
                        .setSql("total_size = total_size + " + picture.getPictureSize())
                        .setSql("total_count = total_count + 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            return picture;
        });
        return PictureVo.objToVo(picture);
    }

    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        String searchText = pictureUploadByBatchRequest.getSearchText();
        if (StrUtil.isBlank(searchText)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "关键词不能为空");
        }
        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        if (StrUtil.isBlank(namePrefix)) {
            namePrefix = searchText;
        }
        // 格式化数量
        Integer count = pictureUploadByBatchRequest.getCount();
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "最多30条");
        // 要抓取的地址
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("获取页面失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
        }
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isNull(div)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");
        }
        // Elements imgElementList = div.select("img.mimg");
        Elements imgElementList = div.select(".iusc");
        int uploadCount = 0;
        int cnt = 0;
        // 查找数据库中是否有同名图片
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", namePrefix)
                .eq("space_id", 0L)
                .orderByDesc("id")
                .last("limit 1");
        Picture tempPicture = this.getOne(queryWrapper);
        if (tempPicture != null) {
            String tempName = tempPicture.getName();
            int strCount = namePrefix.length();
            // 目前数据库中存储的同名图片最大值
            cnt = Integer.parseInt(StrUtil.subSuf(tempName, strCount));
        }
        for (Element imgElement : imgElementList) {
            // String fileUrl = imgElement.attr("src");
            // 获取data-m属性中的JSON字符串
            String dataM = imgElement.attr("m");
            String fileUrl;
            try {
                // 解析JSON字符串
                JSONObject jsonObject = JSONUtil.parseObj(dataM);
                // 获取murl字段（原始图片URL）
                fileUrl = jsonObject.getStr("murl");
            } catch (Exception e) {
                log.error("解析图片数据失败", e);
                continue;
            }
            if (StrUtil.isBlank(fileUrl)) {
                log.info("当前链接为空，已跳过: {}", fileUrl);
                continue;
            }
            // 处理图片上传地址，防止出现转义问题
            int questionMarkIndex = fileUrl.indexOf("?");
            if (questionMarkIndex > -1) {
                fileUrl = fileUrl.substring(0, questionMarkIndex);
            }
            // 上传图片
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            if (StrUtil.isNotBlank(namePrefix)) {
                // 设置图片名称，序号连续递增
                pictureUploadRequest.setPictureName(namePrefix + (uploadCount + cnt + 1));
            }
            try {
                PictureVo pictureVo = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
                log.info("图片上传成功, id = {}", pictureVo.getId());
                uploadCount++;
            } catch (Exception e) {
                log.error("图片上传失败", e);
                continue;
            }
            if (uploadCount >= count) {
                break;
            }
        }
        return uploadCount;
    }


    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long spaceId = pictureQueryRequest.getSpaceId();
//        Boolean nullSpaceId = pictureQueryRequest.getNullSpaceId();
        boolean nullSpaceId;
        nullSpaceId = spaceId == null;
        Long pictureSize = pictureQueryRequest.getPictureSize();
        Integer pictureWidth = pictureQueryRequest.getPictureWidth();
        Integer pictureHeight = pictureQueryRequest.getPictureHeight();
        Double pictureScale = pictureQueryRequest.getPictureScale();
        String pictureFormat = pictureQueryRequest.getPictureFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        Date startEditTime = pictureQueryRequest.getStartEditTime();
        Date endEditTime = pictureQueryRequest.getEndEditTime();
        queryWrapper.ge(ObjUtil.isNotEmpty(startEditTime), "edit_time", startEditTime);
        queryWrapper.lt(ObjUtil.isNotEmpty(endEditTime), "edit_time", endEditTime);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "review_status", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "review_message", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewer_id", reviewerId);
        if (searchText != null) {
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "user_id", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(pictureFormat), "picture_format", pictureFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(pictureSize), "picture_size", pictureSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(pictureWidth), "picture_width", pictureWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(pictureHeight), "picture_height", pictureHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(pictureScale), "picture_scale", pictureScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "space_id", spaceId);
        // 动态分表后改的代码
        queryWrapper.eq(nullSpaceId, "space_id", 0L);
//        queryWrapper.isNull(nullSpaceId, "space_id");
        // Json数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like(StrUtil.isNotBlank(tag), "tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public PictureVo getPictureVo(Picture picture, HttpServletRequest request) {
        PictureVo pictureVo = PictureVo.objToVo(picture);
        Long userId = pictureVo.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVo userVo = userService.getUserVo(user);
            pictureVo.setUser(userVo);
        }
        return pictureVo;
    }

    @Override
    public Page<PictureVo> getPictureVoPage(Page<Picture> picturePage, HttpServletRequest request, String pictureColor) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVo> pictureVoPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVoPage;
        }
        // 如果有颜色参数，则根据颜色算法排序
        if (StrUtil.isNotBlank(pictureColor)) {
            pictureList = this.orderPictureByColor(pictureList, pictureColor);
        }
        // 对象列表 => 封装对象列表
        List<PictureVo> pictureVoList = pictureList.stream().map(PictureVo::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        // todo 好复杂的代码，有空看下java8的stream和基础的set
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVoList.forEach(pictureVo -> {
            Long userId = pictureVo.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVo.setUser(userService.getUserVo(user));
        });
        pictureVoPage.setRecords(pictureVoList);
        return pictureVoPage;
    }

    @Override
    @Deprecated // 使用下面的带空间id的
    public boolean deletePicture(Long pictureId, User loginUser) {
        // 判断是否存在
        Picture oldPicture = this.getById(pictureId);
        if (oldPicture == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 校验权限, 已经使用Sa-token进行全局鉴权
//        this.checkPictureAuth(loginUser, oldPicture);
        // 开启事务
        transactionTemplate.execute(status -> {
            // 操作数据库
            boolean result = this.removeById(pictureId);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "数据库删除失败");
            // 释放额度
            Long spaceId = oldPicture.getSpaceId();
            if (spaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, spaceId)
                        .setSql("total_size = total_size - " + oldPicture.getPictureSize())
                        .setSql("total_count = total_count - 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            return true;
        });
        // 异步清理文件
        this.clearPictureFile(oldPicture);
        return true;
    }

    @Override
    public boolean deletePicture(Long pictureId, Long spaceId, User loginUser) {
        // 判断是否存在
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", pictureId)         // 根据主键 id 查询
                .eq("space_id", spaceId); // 附加 spaceId 条件
        Picture oldPicture = this.getOne(queryWrapper);
        if (oldPicture == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 校验权限, 已经使用Sa-token进行全局鉴权
//        this.checkPictureAuth(loginUser, oldPicture);
        // 开启事务
        transactionTemplate.execute(status -> {
            // 操作数据库
            boolean result = this.remove(queryWrapper);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "数据库删除失败");
            // 释放额度
            if (spaceId != 0) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, spaceId)
                        .setSql("total_size = total_size - " + oldPicture.getPictureSize())
                        .setSql("total_count = total_count - 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            return true;
        });
        // 异步清理文件
        this.clearPictureFile(oldPicture);
        return true;
    }

    @Override
    @Deprecated
    public boolean deletePictures(List<Picture> pictureList, User loginUser) {
        // 开启事务
        transactionTemplate.execute(status -> {
            for (Picture picture : pictureList) {
                Long pictureId = picture.getId();
                // 判断是否存在
                // todo 团队空间的id会查到多个值，添加spaceid
                Picture oldPicture = this.getById(pictureId);
                if (oldPicture == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
                }
                // 校验权限, 已经使用Sa-token进行全局鉴权
//                this.checkPictureAuth(loginUser, oldPicture);
                // 操作数据库
                boolean result = this.removeById(pictureId);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "数据库删除失败");
                // 释放额度
                Long spaceId = oldPicture.getSpaceId();
                if (spaceId != null) {
                    boolean update = spaceService.lambdaUpdate()
                            .eq(Space::getId, spaceId)
                            .setSql("total_size = total_size - " + oldPicture.getPictureSize())
                            .setSql("total_count = total_count - 1")
                            .update();
                    ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
                }
            }
            // 清理所有文件
            this.clearPictureFiles(pictureList);
            return true;
        });
        return true;
    }

    @Override
    public boolean deletePictures(List<Picture> pictureList, Long spaceId, User loginUser) {
        // 开启事务
        transactionTemplate.execute(status -> {
            for (Picture picture : pictureList) {
                Long pictureId = picture.getId();
                // 判断是否存在
                QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", pictureId)         // 根据主键 id 查询
                        .eq("space_id", spaceId); // 附加 spaceId 条件
                Picture oldPicture = this.getOne(queryWrapper);
                if (oldPicture == null) {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
                }
                // 校验权限, 已经使用Sa-token进行全局鉴权
//                this.checkPictureAuth(loginUser, oldPicture);
                // 操作数据库
                boolean result = this.remove(queryWrapper);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "数据库删除失败");
                // 释放额度
                if (spaceId != 0) {
                    boolean update = spaceService.lambdaUpdate()
                            .eq(Space::getId, spaceId)
                            .setSql("total_size = total_size - " + oldPicture.getPictureSize())
                            .setSql("total_count = total_count - 1")
                            .update();
                    ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
                }
            }
            // 清理所有文件
            this.clearPictureFiles(pictureList);
            return true;
        });
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void doPictureDeleteByBatch(PictureDeleteByBatchRequest pictureDeleteByBatchRequest, User loginUser) {
        List<Long> pictureIdList = pictureDeleteByBatchRequest.getPictureIdList();
        Long spaceId = pictureDeleteByBatchRequest.getSpaceId();
        // 补充空间 id，默认为 0
        if (spaceId == null) {
            spaceId = 0L;
        }
        // 1. 校验参数
        // spaceId可以为0，为个人上传图片页面做准备
        ThrowUtils.throwIf(CollUtil.isEmpty(pictureIdList), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH);
        // 2. 校验权限
//        if (spaceId != null) {
        if (spaceId != 0) {
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(ObjUtil.isEmpty(space), ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            this.checkSpaceAuth(loginUser, space);
        } else {
            this.checkSpaceAuth(loginUser, null);
        }
        // 3. 查询指定图片，仅选择需要的字段
        List<Picture> pictureList;
//        if (spaceId != null) {
//            pictureList = this.lambdaQuery()
//                    .select(Picture::getId, Picture::getSpaceId)
//                    .eq(Picture::getSpaceId, spaceId)
//                    .in(Picture::getId, pictureIdList)
//                    .list();
//        } else {
//            pictureList = this.lambdaQuery()
//                    .select(Picture::getId)
//                    .isNull(Picture::getSpaceId)
//                    .in(Picture::getId, pictureIdList)
//                    .list();
//        }
        pictureList = this.lambdaQuery()
                .select(Picture::getId, Picture::getSpaceId)
                .eq(Picture::getSpaceId, spaceId)
                .in(Picture::getId, pictureIdList)
                .list();
        if (pictureList.isEmpty()) {
            return;
        }
        // 4. 批量删除
//        boolean result = this.deletePictures(pictureList, loginUser);
        boolean result = this.deletePictures(pictureList, spaceId, loginUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        this.validPicture(picture);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Long spaceId = pictureEditRequest.getSpaceId();
        // todo 前端记得改一下，团队空间，和私人空间得传入spaceId
        if (spaceId == null) {
            spaceId = 0L;
        }
        // 构造 QueryWrapper
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)         // 根据主键 id 查询
                .eq("space_id", spaceId); // 附加 spaceId 条件
        Picture oldPicture = this.getOne(queryWrapper);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验权限, 已经使用Sa-token进行全局鉴权
//        this.checkPictureAuth(loginUser, oldPicture);
        // 补充审核参数
        this.fillReviewParams(picture, loginUser);
        // 构造 UpdateWrapper
        UpdateWrapper<Picture> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", picture.getId()) // 指定主键条件，批量更新则使用 in 传递多条
                .eq("space_id", spaceId);      // 补充条件 spaceId=xxx

        // 操作数据库
        boolean result = this.update(picture, updateWrapper);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
        List<Long> pictureIdList = pictureEditByBatchRequest.getPictureIdList();
        Long spaceId = pictureEditByBatchRequest.getSpaceId();
        String category = pictureEditByBatchRequest.getCategory();
        List<String> tags = pictureEditByBatchRequest.getTags();
        String nameRule = pictureEditByBatchRequest.getNameRule();
        if (spaceId == null) {
            spaceId = 0L;
        }
        // 1. 校验参数
        // spaceId可以为空，为个人上传图片页面做准备
        ThrowUtils.throwIf(CollUtil.isEmpty(pictureIdList), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH);
        // 2. 校验权限
//        if (spaceId != null) {
        if (spaceId != 0) {
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(ObjUtil.isEmpty(space), ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            this.checkSpaceAuth(loginUser, space);
        } else {
            this.checkSpaceAuth(loginUser, null);
        }
        // 3. 查询指定图片，仅选择需要的字段
        List<Picture> pictureList;
//        if (spaceId != null) {
//            pictureList = this.lambdaQuery()
//                    .select(Picture::getId, Picture::getSpaceId)
//                    .eq(Picture::getSpaceId, spaceId)
//                    .in(Picture::getId, pictureIdList)
//                    .list();
//        } else {
//            pictureList = this.lambdaQuery()
//                    .select(Picture::getId)
//                    .isNull(Picture::getSpaceId)
//                    .in(Picture::getId, pictureIdList)
//                    .list();
//        }
        pictureList = this.lambdaQuery()
                .select(Picture::getId, Picture::getSpaceId)
                .eq(Picture::getSpaceId, spaceId)
                .in(Picture::getId, pictureIdList)
                .list();
        if (pictureList.isEmpty()) {
            return;
        }
        // 4. 更新分类和标签
        pictureList.forEach(picture -> {
            if (StrUtil.isNotBlank(category)) {
                picture.setCategory(category);
            }
            if (CollUtil.isNotEmpty(tags)) {
                picture.setTags(JSONUtil.toJsonStr(tags));
            }
        });
        // 5. 更新重命名
        this.fillPictureWithNameRule(pictureList, nameRule);
        // 6. 批量更新
        // 开启事务
        Long finalSpaceId = spaceId;
        transactionTemplate.execute(status -> {
            // 构造 UpdateWrapper
            for (Picture picture : pictureList) {
                UpdateWrapper<Picture> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", picture.getId()) // 指定主键条件，批量更新则使用 in 传递多条
                        .eq("space_id", finalSpaceId);      // 补充条件 spaceId=xxx
                boolean result = this.update(picture, updateWrapper);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            }
            return true;
        });
//        Set<Long> pictureIdSet = pictureList.stream().map(Picture::getId).collect(Collectors.toSet());
//        UpdateWrapper<Picture> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.in("id", pictureIdSet) // 指定主键条件，批量更新则使用 in 传递多条
//                .eq("space_id", spaceId);      // 补充条件 spaceId=xxx
//        boolean result = this.update(pictureList, updateWrapper);
//        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }


    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    @Override
    public List<Picture> orderPictureByColor(List<Picture> pictureList, String pictureColor) {
        // 如果没有图片，直接返回空列表
        if (CollUtil.isEmpty(pictureList)) {
            return Collections.emptyList();
        }
        // 将目标颜色转为 Color 对象
        Color targetColor = Color.decode(pictureColor);
        // 4. 计算相似度并排序
        List<Picture> sortedPictures = pictureList.stream()
                .sorted(Comparator.comparingDouble(picture -> {
                    // 提取图片主色调
                    String hexColor = picture.getPictureColor();
                    // 没有主色调的图片放到最后
                    if (StrUtil.isBlank(hexColor)) {
                        return Double.MAX_VALUE;
                    }
                    Color color = Color.decode(hexColor);
                    // 越大越相似
                    return -ColorSimilarUtils.calculateSimilarity(targetColor, color);
                }))
                // 取前 12 个
//                .limit(12)
                .collect(Collectors.toList());


//        // 转换为 PictureVO
//        return sortedPictures.stream()
//                .map(PictureVO::objToVo)
//                .collect(Collectors.toList());
        return sortedPictures;
    }

    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        if (id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        // 默认是公共空间，空间id为0；
        Long spaceId = 0L;
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)         // 根据主键 id 查询
                .eq("space_id", spaceId); // 附加 spaceId 条件
        Picture oldPicture = this.getOne(queryWrapper);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 已是该状态
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复审核");
        }
        // 更新审核状态
        Picture updatePicture = new Picture();
        BeanUtils.copyProperties(pictureReviewRequest, updatePicture);
        updatePicture.setReviewerId(loginUser.getId());
        updatePicture.setReviewTime(new Date());
        // 构造 UpdateWrapper
        UpdateWrapper<Picture> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", updatePicture.getId()) // 指定主键条件，批量更新则使用 in 传递多条
                .eq("space_id", spaceId);      // 补充条件 spaceId=xxx
        boolean result = this.update(updatePicture, updateWrapper);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void doPictureReviewByBatch(PictureReviewByBatchRequest pictureReviewByBatchRequest, User loginUser) {
        List<Long> pictureIdList = pictureReviewByBatchRequest.getPictureIdList();
        Integer reviewStatus = pictureReviewByBatchRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        String reviewMessage = pictureReviewByBatchRequest.getReviewMessage();
        // 1. 校验参数
        ThrowUtils.throwIf(CollUtil.isEmpty(pictureIdList), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH);
        ThrowUtils.throwIf(reviewStatusEnum == null, ErrorCode.NO_AUTH);
        if (StrUtil.isBlank(reviewMessage)) {
            reviewMessage = "管理员过审";
        }
        // 2. 校验权限
        this.checkSpaceAuth(loginUser, null);
        // 3. 查询指定图片，仅选择需要的字段
        List<Picture> pictureList;
        pictureList = this.lambdaQuery()
                .select(Picture::getId)
                .eq(Picture::getSpaceId, 0L)
//                .isNull(Picture::getSpaceId)
                .in(Picture::getId, pictureIdList)
                .list();
        if (pictureList.isEmpty()) {
            return;
        }
        // 4. 更新过审
        String finalReviewMessage = reviewMessage;
//        pictureList.forEach(picture -> {
//            picture.setReviewStatus(reviewStatusEnum.getValue());
//            picture.setReviewMessage(finalReviewMessage);
//            picture.setReviewerId(loginUser.getId());
//            picture.setReviewTime(new Date());
//        });
        Picture updatePicture = new Picture();
        updatePicture.setReviewStatus(reviewStatusEnum.getValue());
        updatePicture.setReviewMessage(finalReviewMessage);
        updatePicture.setReviewerId(loginUser.getId());
        updatePicture.setReviewTime(new Date());
        // 7. 批量更新
        // 构造 UpdateWrapper
        Set<Long> pictureIdSet = pictureList.stream().map(Picture::getId).collect(Collectors.toSet());
        UpdateWrapper<Picture> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", pictureIdSet) // 指定主键条件，批量更新则使用 in 传递多条
                .eq("space_id", 0L);      // 补充条件 spaceId=xxx
        boolean result = this.update(updatePicture, updateWrapper);
//        boolean result = this.update(pictureList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            // 管理员自动过审
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewTime(new Date());
            picture.setReviewMessage("管理员自动过审");
        } else {
            // 非管理员创建或者编辑都需要改成待审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    @Override
    @Deprecated // 已经使用Sa-token进行全局鉴权
    public void checkPictureAuth(User loginUser, Picture picture) {
        Long spaceId = picture.getSpaceId();
        if (spaceId == null) {
            // 公共图库，仅本人或管理员可操作
            if (!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
        } else {
            // 私有图库，仅本人可以操作
            if (!picture.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
        }
    }

    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        if (space == null) {
            // 公共图库，仅本人或管理员可操作
            // todo 好像有问题，记得改一下
            if (!loginUser.getId().equals(space.getUserId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
        } else {
            // 私有图库，仅本人可以操作
            if (!loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH, "没有空间访问权限");
            }
        }
    }

    /**
     * 清理COS对象存储中的图片资源
     *
     * @param oldPicture
     */
    @Async
    @Override
    public void clearPictureFile(Picture oldPicture) {
        // 判断该图片是否被多条记录使用
        String pictureUrl = oldPicture.getUrl();
        long count = this.lambdaQuery()
                .eq(Picture::getUrl, pictureUrl)
                .count();
        // 有不止一条记录用到了该图片，不清理
        if (count > 1) {
            return;
        }
        // 注意，这里的 url 包含了域名，实际上只要传 key 值（存储路径）就够了
        int hostLength = cosClientConfig.getHost().length();
        String urlKey = StrUtil.subSuf(pictureUrl, hostLength + 1);
        cosManager.deleteObject(urlKey);
        // 清理缩略图
        String thumbnailUrl = oldPicture.getThumbnailUrl();
        if (StrUtil.isNotBlank(thumbnailUrl)) {
            String thumbnailKey = StrUtil.subSuf(thumbnailUrl, hostLength + 1);
            cosManager.deleteObject(thumbnailKey);
        }
        // 清理原图
        String originalUrl = oldPicture.getOriginalUrl();
        if (StrUtil.isNotBlank(originalUrl)) {
            String originalKey = StrUtil.subSuf(originalUrl, hostLength + 1);
            cosManager.deleteObject(originalKey);
        }
    }

    /**
     * 批量清理COS对象存储中的图片资源
     * 先不使用异步，注销空间必须删除干净资源
     *
     * @param pictureList
     */
    @Override
    public void clearPictureFiles(List<Picture> pictureList) {
        List<String> keyList = new ArrayList<>();
        for (Picture oldPicture : pictureList) {
            // 判断该图片是否被多条记录使用
            String pictureUrl = oldPicture.getUrl();
            long count = this.lambdaQuery()
                    .eq(Picture::getUrl, pictureUrl)
                    .count();
            // 有不止一条记录用到了该图片，不清理
            if (count > 1) {
                return;
            }
            // 注意，这里的 url 包含了域名，实际上只要传 key 值（存储路径）就够了
            int hostLength = cosClientConfig.getHost().length();
            String urlKey = StrUtil.subSuf(pictureUrl, hostLength + 1);
            keyList.add(urlKey);
            // 清理缩略图
            String thumbnailUrl = oldPicture.getThumbnailUrl();
            if (StrUtil.isNotBlank(thumbnailUrl)) {
                String thumbnailKey = StrUtil.subSuf(thumbnailUrl, hostLength + 1);
                keyList.add(thumbnailKey);
            }
            // 清理原图
            String originalUrl = oldPicture.getOriginalUrl();
            if (StrUtil.isNotBlank(originalUrl)) {
                String originalKey = StrUtil.subSuf(originalUrl, hostLength + 1);
                keyList.add(originalKey);
            }
        }
        cosManager.deleteObjects(keyList);
    }

    /**
     * nameRule 格式：图片{序号}
     *
     * @param pictureList
     * @param nameRule
     */
    private void fillPictureWithNameRule(List<Picture> pictureList, String nameRule) {
        if (CollUtil.isEmpty(pictureList) || StrUtil.isBlank(nameRule)) {
            return;
        }
        long count = 1;
        try {
            for (Picture picture : pictureList) {
                String pictureName = nameRule.replaceAll("\\{序号}", String.valueOf(count++));
                picture.setName(pictureName);
            }
        } catch (Exception e) {
            log.error("名称解析错误", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "名称解析错误");
        }
    }

}




