package com.keriko.example.provider;

import com.keriko.echorpc.bootstrap.ProviderBootstrap;
import com.keriko.echorpc.model.ServiceRegisterInfo;
import com.keriko.example.common.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class ProviderBootstrapExample {
    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
