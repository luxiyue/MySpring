package com.lzl.userapp.controller;

import com.alibaba.fastjson.JSON;
import com.lzl.springmvc.annotations.Controller;
import com.lzl.springmvc.annotations.RequestMapping;
import com.lzl.springmvc.annotations.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public String test02(@RequestParam("list") List<Integer> list){
        return "TestController.test02~" + list.size() + " " + JSON.toJSONString(list);
    }

    @RequestMapping("/test3")
    public String test03(@RequestParam("set") Set<Integer> set){
        return set.size() + "  " + set.contains(1) + "   " + set.contains("1");
    }


    @RequestMapping("/test4")
    public String test04(){
        return "lzl nb~";
    }


}
