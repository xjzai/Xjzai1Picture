package com.xjzai1.xjzai1picturebackend.controller;

import com.xjzai1.xjzai1picturebackend.common.BaseResponse;
import com.xjzai1.xjzai1picturebackend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
