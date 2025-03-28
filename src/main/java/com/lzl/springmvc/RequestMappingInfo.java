package com.lzl.springmvc;

import java.lang.reflect.Method;

/**
 * @Author： Luzelong
 * @Created： 2023/10/27 15:14
 */
public class RequestMappingInfo {

    private Method method;
    private Object obj;
    private String url;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
