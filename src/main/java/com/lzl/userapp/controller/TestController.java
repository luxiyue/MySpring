package com.lzl.userapp.controller;

import com.alibaba.fastjson.JSON;
import com.lzl.springmvc.annotations.Controller;
import com.lzl.springmvc.annotations.RequestMapping;
import com.lzl.springmvc.annotations.RequestParam;

import java.util.List;

/**
 * @Author： Luzelong
 * @Created： 2023/10/26 20:05
 */
@Controller("testController")
public class TestController {

    @RequestMapping("/test")
    public String test01(@RequestParam("name") String name){
        return "TestController.test01~" + name;
    }

    @RequestMapping("/test2")
    public String test02(@RequestParam("list")List<Integer> list){
        return "TestController.test02~" + list.size() + " " + JSON.toJSONString(list);
    }

}
