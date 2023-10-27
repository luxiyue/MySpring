package com.lzl.springmvc.handlerMappings;

import com.lzl.spring.annotations.Component;
import com.lzl.springmvc.HandlerExecutionChain;
import com.lzl.springmvc.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author： Luzelong
 * @Created： 2023/10/27 14:00
 */
@Component
public class RequestMappingHandlerMapping implements HandlerMapping {


    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        return null;
    }


}
