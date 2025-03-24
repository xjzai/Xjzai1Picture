package com.xjzai1.xjzai1picturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.xjzai1.xjzai1picturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true) // 通过SpringAOP提供对当前代理对象的访问，使得可以在业务逻辑中访问到当前的代理对象
public class Xjzai1PictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(Xjzai1PictureBackendApplication.class, args);
    }

}
