package com.lzl.userapp.service.impl;

import com.lzl.spring.*;
import com.lzl.userapp.service.UserInterface;

@Component("userService")
@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean, UserInterface {
    @Autowired
    private OrderService orderService;

    private String beanName;

    private String xxx;

    @Override
    public void afterPropertiesSet() {
        this.xxx = "luzelongXXX";
        System.out.println("UserService.afterPropertiesSet() -- execute!!!!");
    }

    @Override
    public void test(){
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getXxx() {
        return xxx;
    }
}
