package com.keriko.examplespringbootconsumer;

import com.keriko.echorpc.proxy.ServiceProxyFactory;
import com.keriko.echorpc.springboot.starter.annotation.EnableRpc;
import com.keriko.example.common.model.User;
import com.keriko.example.common.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
@EnableRpc(needServer = false)
public class ExampleSpringbootConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootConsumerApplication.class, args);

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("keriko");
        User newUser = userService.getUser(user);
        if(newUser != null){
            System.out.println(newUser.getName());
        } else {
            System.out.println("user is null");
        }
    }

}
