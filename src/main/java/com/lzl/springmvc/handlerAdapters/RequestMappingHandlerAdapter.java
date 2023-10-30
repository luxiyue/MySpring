package com.lzl.springmvc.handlerAdapters;

import com.alibaba.fastjson.JSON;
import com.lzl.spring.annotations.Component;
import com.lzl.springmvc.HandlerAdapter;
import com.lzl.springmvc.RequestMappingInfo;
import com.lzl.springmvc.annotations.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
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
        Object returnValue = invokeForRequest(request, (RequestMappingInfo) handler);
        handleReturnValue(response, returnValue);
        return null;
    }

    private Object invokeForRequest(HttpServletRequest request, RequestMappingInfo handler) throws IllegalAccessException, InvocationTargetException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        RequestMappingInfo info = handler;
        Method method = info.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();

        Object[] paramValues = new Object[parameters.length];
        for (int i=0; i<parameters.length;i++){
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()){
                if (entry.getKey().equals(parameters[i].getAnnotation(RequestParam.class).value())) {
                    //FIXME：第一个if只是为了简单实现数组类型的基本参数，后续扩展应该去掉，放在参数映射器中处理
                    if (Collection.class.isAssignableFrom(parameterTypes[i])) {
                        paramValues[i] = (Collection)Arrays.asList(entry.getValue()[0].split(","));
                    }else {
                        paramValues[i] = entry.getValue()[0];
                    }

                }
            }
        }

        return method.invoke(info.getObj(), paramValues);
    }




    //目前只简单实现源码中的 RequestResponseBodyMethodProcessor 的 handleReturnValue
    private void handleReturnValue(HttpServletResponse response, Object returnValue) throws IOException {
        response.getWriter().write(JSON.toJSONString(returnValue));
    }

}
