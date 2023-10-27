package com.lzl.springmvc.handlerMappings;

import com.lzl.spring.annotations.Component;
import com.lzl.springmvc.HandlerExecutionChain;
import com.lzl.springmvc.HandlerMapping;
import com.lzl.springmvc.RequestMappingInfo;
import com.lzl.springmvc.annotations.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author： Luzelong
 * @Created： 2023/10/27 14:00
 */
@Component
public class RequestMappingHandlerMapping implements HandlerMapping {

    private static Map<String,HandlerExecutionChain> map = new HashMap<>();

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        return map.get(request.getRequestURI());
    }


    @Override
    public Object postProcessAfterInitialization(String beanName, Object bean) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        if (methods != null && methods.length != 0) {
            for (Method method : methods) {
                HandlerExecutionChain handlerExecutionChain = createHandlerExecutionChain(method, bean);
                if (handlerExecutionChain != null) {
                    RequestMappingInfo info = (RequestMappingInfo)handlerExecutionChain.getHandler();
                    map.put(info.getUrl(), handlerExecutionChain);
                }
            }
        }
        return bean;
    }

    private HandlerExecutionChain createHandlerExecutionChain(Method method, Object bean) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            HandlerExecutionChain handlerExecutionChain = new HandlerExecutionChain();
            RequestMappingInfo info = new RequestMappingInfo();
            info.setMethod(method);
            info.setObj(bean);
            info.setUrl(method.getDeclaredAnnotation(RequestMapping.class).value());
            handlerExecutionChain.setHandler(info);
            return handlerExecutionChain;
        }
        return null;
    }

}
