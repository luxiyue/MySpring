package com.lzl.springmvc.annotations;

import com.lzl.spring.annotations.Component;

import java.lang.annotation.*;

/**
 * @Author： Luzelong
 * @Created： 2023/10/26 19:58
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {

    String value() default "";

}
