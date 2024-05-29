package com.keriko.example.common.model;

import java.io.Serializable;


/**
 * User类实现了Serializable接口，用于表示用户信息。
 */
public class User implements Serializable {

    /**
     * 获取用户的名字。
     * @return 返回用户的名字。
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户的名字。
     * @param name 要设置的用户名字。
     */
    public void setName(String name) {
        this.name = name;
    }

    // 用户的名字
    private String name;

    // 序列化ID
    private static final long serialVersionUID = 1L;
}
