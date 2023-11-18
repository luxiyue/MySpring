package com.lzl.springmvc.handlerAdapters;

import com.alibaba.fastjson.JSON;
import com.lzl.spring.annotations.Component;
import com.lzl.springmvc.HandlerAdapter;
import com.lzl.springmvc.RequestMappingInfo;
import com.lzl.springmvc.annotations.RequestParam;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
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
        Parameter[] parameters = method.getParameters();
        if (parameters == null || parameters.length == 0) {
            return new Object[0];
        }
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Object[] args = new Object[parameters.length];
        for (int i=0; i<parameters.length;i++){
            // 模拟参数解析器对参数进行解析
            args[i] = resolveArgument(genericParameterTypes[i], parameters[i] ,request);
        }

        return args;
    }


    // 模拟 RequestParamMethodArgumentResolver 这个参数处理器
    private static Object resolveArgument(Type type, Parameter parameter, HttpServletRequest request) {
        //参数名字
        String resolvedName =  parameter.getAnnotation(RequestParam.class).value();
        String[] paramValues = request.getParameterValues(resolvedName);
        Object arg = null;
        if (paramValues != null) {
            arg =  (paramValues.length == 1 ? paramValues[0] : paramValues);
        }

        //简单模拟参数绑定器，处理Collection类型的参数
        arg = convertIfNecessary(type, arg);

        return arg;
    }




    // 我这边是考虑到了参数的范型，所以复杂了点
    private static Object convertIfNecessary(Type type, Object arg) {
        Class parameterType = null;
        Type[] actualTypeArguments = null;
        boolean haveFan = false;
        try {
            //参数有范型
            ParameterizedTypeImpl typeImpl = (ParameterizedTypeImpl) type;
            actualTypeArguments = typeImpl.getActualTypeArguments();
            parameterType = typeImpl.getRawType();
            haveFan = true;
        }catch (Exception e) {
            //参数没有范型
            parameterType = (Class) type;
        }
        if (Collection.class.isAssignableFrom(parameterType) && arg instanceof String) {
            if (!haveFan) {
                arg = JSON.parseObject(JSON.toJSONString(((String) arg).split(",")), parameterType);
            } else {
                Class fanxing = (Class) actualTypeArguments[0];
                arg = JSON.parseObject(JSON.toJSONString(JSON.parseArray(JSON.toJSONString(((String) arg).split(","))).toJavaList(fanxing)), parameterType);
            }
        }
        return arg;
    }




    // 模拟RequestResponseBodyMethodProcessor这个返回值处理器
    private void handleReturnValue(HttpServletResponse response, Object returnValue) throws IOException {
        response.getWriter().write(JSON.toJSONString(returnValue));
    }

}
