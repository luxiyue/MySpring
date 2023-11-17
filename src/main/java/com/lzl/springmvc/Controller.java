package com.lzl.springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author： Luzelong
 * @Created： 2023/11/17 17:09
 */
public interface Controller {

    Object handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
