package com.lzl.springmvc.annotations;

import java.lang.annotation.*;

/**
 * @Author： Luzelong
 * @Created： 2023/10/26 19:59
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value() default "";

}
