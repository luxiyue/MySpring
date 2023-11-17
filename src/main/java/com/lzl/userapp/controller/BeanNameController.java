package com.lzl.userapp.controller;

import com.lzl.spring.annotations.Component;
import com.lzl.springmvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author： Luzelong
 * @Created： 2023/11/17 17:22
 */
@Component("/lzl")
public class BeanNameController implements Controller {

    @Override
    public Object handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.getOutputStream().write("lzl万岁！".getBytes());
        return null;
    }

}
