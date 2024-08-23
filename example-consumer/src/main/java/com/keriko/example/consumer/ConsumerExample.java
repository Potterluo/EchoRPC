package com.keriko.example.consumer;

import com.keriko.example.common.model.User;
import com.keriko.example.common.service.UserService;
import com.keriko.echorpc.bootstrap.ConsumerBootstrap;
import com.keriko.echorpc.proxy.ServiceProxyFactory;

/**
 * 服务消费者示例
 */
public class ConsumerExample {

    public static void main(String[] args) {
        // 服务提供者初始化
        ConsumerBootstrap.init();

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("keriko");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
