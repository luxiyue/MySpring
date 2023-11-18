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
import java.util.Collection;

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
        //模拟返回值处理器
        handleReturnValue(response, returnValue);
        return null;
    }


    private Object invokeForRequest(HttpServletRequest request, RequestMappingInfo handler) throws IllegalAccessException, InvocationTargetException {
        Object[] args = getMethodArgumentValues(request,handler);
        //模拟 doInvoke
        return handler.getMethod().invoke(handler.getObj(), args);
    }


    public static Object[] getMethodArgumentValues(HttpServletRequest request, RequestMappingInfo handler){
        Method method = handler.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();

        Object[] args = new Object[parameters.length];
        for (int i=0; i<parameters.length;i++){
            // 模拟参数解析器对参数进行解析
            args[i] = resolveArgument( parameterTypes[i], parameters[i] ,request);
        }

        return args;
    }


    // 模拟 RequestParamMethodArgumentResolver 这个参数处理器
    private static Object resolveArgument(Class<?> parameterType, Parameter parameter, HttpServletRequest request) {
        //参数名字
        String resolvedName =  parameter.getAnnotation(RequestParam.class).value();
        String[] paramValues = request.getParameterValues(resolvedName);
        Object arg = null;
        if (paramValues != null) {
            arg =  (paramValues.length == 1 ? paramValues[0] : paramValues);
        }

        //简单模拟参数绑定器，处理Collect类型的参数
        if (Collection.class.isAssignableFrom(parameterType) && arg instanceof String) {
            arg = JSON.parseObject(JSON.toJSONString(((String)arg).split(",")), parameterType)  ;
        }

        return arg;
    }


    // 模拟RequestResponseBodyMethodProcessor这个返回值处理器
    private void handleReturnValue(HttpServletResponse response, Object returnValue) throws IOException {
        response.getWriter().write(JSON.toJSONString(returnValue));
    }

}
