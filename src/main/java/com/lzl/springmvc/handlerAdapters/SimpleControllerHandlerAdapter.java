package com.lzl.springmvc.handlerAdapters;

import com.lzl.spring.annotations.Component;
import com.lzl.springmvc.Controller;
import com.lzl.springmvc.HandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author： Luzelong
 * @Created： 2023/11/17 17:15
 */
@Component
public class SimpleControllerHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof Controller;
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return ((Controller) handler).handleRequest(request, response);
    }

}
