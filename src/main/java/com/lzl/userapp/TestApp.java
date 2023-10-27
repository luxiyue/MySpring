package com.lzl.userapp;

import com.lzl.spring.LzlApplicationContext;
import com.lzl.userapp.config.AppConfig;
import com.lzl.userapp.controller.TestController;
import com.lzl.userapp.service.impl.UserService;

public class TestApp {
    public static void main(String[] args) {
        LzlApplicationContext applicationContext = new LzlApplicationContext(AppConfig.class);
//        UserService userService = applicationContext.getBean(UserService.class);
//        userService.test();
//        System.out.println(userService.getXxx());

        TestController bean = applicationContext.getBean(TestController.class);
        System.out.println(bean);
    }
}
