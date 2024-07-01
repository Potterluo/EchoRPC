package com.keriko.example.provider;

import com.keriko.example.common.model.User;
import com.keriko.example.common.service.UserService;



/**
 * UserServiceImpl 类实现了 UserService 接口，提供用户服务的实现。
 */
public class UserServiceImpl implements UserService {

    /**
     * 根据传入的用户对象获取用户信息。
     *
     * @param user 一个包含用户信息的 User 对象。
     * @return 返回与传入 User 对象相同的用户对象。
     */
    public User getUser(User user) {
        // 打印用户名称
        System.out.println("这里是Provider，获得参数用户名：" + user.getName());
        user.setName("Provider");
        return user;
    }
}

