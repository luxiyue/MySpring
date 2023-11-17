package com.lzl.springmvc.handlerMappings;

import com.lzl.spring.annotations.Component;
import com.lzl.springmvc.Controller;
import com.lzl.springmvc.HandlerExecutionChain;
import com.lzl.springmvc.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author： Luzelong
 * @Created： 2023/11/17 17:13
 */
@Component
public class BeanNameUrlHandlerMapping implements HandlerMapping {

    private static Map<String,Object> map = new HashMap<>();


    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Object handler = map.get(request.getRequestURI());
        if (handler != null) {
            HandlerExecutionChain handlerExecutionChain = new HandlerExecutionChain();
            handlerExecutionChain.setHandler(handler);
            return handlerExecutionChain;
        }
        return null;
    }


    @Override
    public Object postProcessAfterInitialization(String beanName, Object bean) {
        if (beanName.startsWith("/") && bean instanceof Controller) {
            map.put(beanName, bean);
        }
        return bean;
    }

}
