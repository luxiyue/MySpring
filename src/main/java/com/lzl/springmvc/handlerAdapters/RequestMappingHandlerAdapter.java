package com.lzl.springmvc.handlerAdapters;

import com.lzl.spring.annotations.Component;
import com.lzl.springmvc.HandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author： Luzelong
 * @Created： 2023/10/27 13:56
 */
@Component
public class RequestMappingHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return false;
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return null;
    }

}
