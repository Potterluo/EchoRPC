package com.keriko.examplespringbootprovider;

import com.keriko.example.common.model.User;
import com.keriko.example.common.service.UserService;
import com.keriko.echorpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        user.setName(user.getName()+ "-provider");
        return user;
    }
}