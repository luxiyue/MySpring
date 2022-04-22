package com.lzl.service;

import com.lzl.spring.Autowired;
import com.lzl.spring.Component;
import com.lzl.spring.Scope;

@Component("userService")
@Scope("prototype")
public class UserService {
    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println(orderService);
    }

}
