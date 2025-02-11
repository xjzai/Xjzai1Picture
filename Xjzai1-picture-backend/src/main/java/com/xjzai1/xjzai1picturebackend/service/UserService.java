package com.xjzai1.xjzai1picturebackend.service;

import com.xjzai1.xjzai1picturebackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjzai1.xjzai1picturebackend.model.dto.UserRegisterRequest;
import com.xjzai1.xjzai1picturebackend.model.vo.UserLoginVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-02-11 00:32:35
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    UserLoginVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 加密密码
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取脱敏的用户信息
     * @param user
     * @return
     */
    UserLoginVo getLoginUserVO(User user);
}
