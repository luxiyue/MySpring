package com.lzl.userapp;

import com.lzl.spring.LzlApplicationContext;
import com.lzl.userapp.config.AppConfig;
import com.lzl.userapp.service.impl.UserService;

public class TestApp {
    public static void main(String[] args) {
        LzlApplicationContext applicationContext = new LzlApplicationContext(AppConfig.class);
        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();
        System.out.println(userService.getXxx());
    }
}
