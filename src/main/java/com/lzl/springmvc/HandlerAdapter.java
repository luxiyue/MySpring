package com.lzl.springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author： Luzelong
 * @Created： 2023/10/27 11:51
 */
public interface HandlerAdapter {

    boolean supports(Object handler);

    /**
     * 源码中是返回modelAndView对象,但由于本代码其实只仿写加了@ResponseBody的情况 （ RequestResponseBodyMethodProcessor ）
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    Object handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

}
