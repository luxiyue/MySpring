package com.lzl.userapp.controller;

import com.lzl.springmvc.annotations.Controller;
import com.lzl.springmvc.annotations.RequestMapping;
import com.lzl.springmvc.annotations.RequestParam;

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

}
