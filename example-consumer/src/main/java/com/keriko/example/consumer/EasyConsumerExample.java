package com.keriko.example.consumer;

import com.keriko.echorpc.config.RpcConfig;
import com.keriko.echorpc.proxy.ServiceProxyFactory;
import com.keriko.echorpc.utils.ConfigUtils;
import com.keriko.example.common.model.User;
import com.keriko.example.common.service.UserService;

/**
 * 简易服务消费者示例
 */
public class EasyConsumerExample {

    public static void main(String[] args) {
        // 静态代理
//        UserService userService = new UserServiceProxy();
        //动态代理
//        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
//        System.out.println(rpcConfig);
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("Consumer");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("这里是Consumer，接收到getUser的返回值"+newUser.getName());
        } else {
            System.out.println("user == null");
        }
        System.out.println("这里是Consumer，接收到getNumber的返回值"+userService.getNumber());

    }
}

