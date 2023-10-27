package com.lzl.springmvc.handlerAdapters;

import com.alibaba.fastjson.JSON;
import com.lzl.spring.annotations.Component;
import com.lzl.springmvc.HandlerAdapter;
import com.lzl.springmvc.RequestMappingInfo;
import com.lzl.springmvc.annotations.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @Author： Luzelong
 * @Created： 2023/10/27 13:56
 */
@Component
public class RequestMappingHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof RequestMappingInfo;
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String[]> parameterMap = request.getParameterMap();
        RequestMappingInfo info = (RequestMappingInfo) handler;
        Method method = info.getMethod();
        Parameter[] parameters = method.getParameters();

        Object[] paramValues = new Object[parameters.length];
        for (int i=0; i<parameters.length;i++){
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()){
                if (entry.getKey().equals(parameters[i].getAnnotation(RequestParam.class).value())) {
                    paramValues[i] = entry.getValue()[0];
                }
            }
        }

        Object invoke = method.invoke(info.getObj(), paramValues);
        response.getWriter().write(JSON.toJSONString(invoke));
        return null;
    }

}
