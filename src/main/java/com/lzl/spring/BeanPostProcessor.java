package com.lzl.spring;

public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(String beanName,Object bean){
        return bean;
    }

    default Object postProcessAfterInitialization(String beanName,Object bean){
        return bean;
    }

}
