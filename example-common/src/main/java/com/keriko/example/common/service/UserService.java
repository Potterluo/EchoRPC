package com.keriko.example.common.service;

import com.keriko.example.common.model.User;

public interface UserService {

    User getUser(User user);

    default short getNumber() {
        return 1;
    }
}
