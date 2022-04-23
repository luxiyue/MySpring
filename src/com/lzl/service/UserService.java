package com.lzl.service;

import com.lzl.spring.*;

@Component("userService")
@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean {
    @Autowired
    private OrderService orderService;

    private String beanName;

    private String xxx;

    @Override
    public void afterPropertiesSet() {
        this.xxx = "luzelongXXX";
        System.out.println("UserService.afterPropertiesSet() -- execute!!!!");
    }

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
