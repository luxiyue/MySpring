package com.lzl.userapp.config;

import com.lzl.spring.BeanPostProcessor;
import com.lzl.spring.annotations.Component;

@Component
public class LuzelongBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(String beanName, Object bean) {
        if (beanName.equals("userService")) {
            System.out.println("LuzelongBeanPostProcessor.before!!!!!");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object bean) {
        //lzl:如果使用下面的JDK代理把对象替换了，可以使用UserInterface中的test()来进行测试切面是否生效，且要注意的是Test类中GetBean需要返回UserInterFace类
        if (beanName.equals("userService")) {
            System.out.println("LuzelongBeanPostProcessor.after!!!!!");
//            Object proxyInstance = Proxy.newProxyInstance(LuzelongBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
//                @Override
//                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                    System.out.println("切面逻辑....");
//                    return method.invoke(bean,args);
//                }
//            });
//            return proxyInstance;
        }
        return bean;
    }
}
