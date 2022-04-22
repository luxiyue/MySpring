package com.lzl.service;

import com.lzl.spring.LzlApplicationContext;

public class Test {
    public static void main(String[] args) {
        LzlApplicationContext applicationContext = new LzlApplicationContext(AppConfig.class);
        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test();
    }
}
