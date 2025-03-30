package com.xjzai1.xjzai1picturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureReviewByBatchRequest implements Serializable {
    /**
     * 图片 id 列表
     */
    private List<Long> pictureIdList;

    /**
     * 状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;


    private static final long serialVersionUID = 1L;
}
