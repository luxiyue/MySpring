package com.lzl.springmvc;

import com.lzl.spring.BeanPostProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author： Luzelong
 * @Created： 2023/10/26 19:58
 */
public interface HandlerMapping extends BeanPostProcessor {

    /**
     * spring源码返回的是 HandlerExecutionChain
     * @param request
     * @return
     * @throws Exception
     */
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

    
}
