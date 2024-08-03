package com.keriko.example.provider;

import cn.hutool.core.net.NetUtil;
import com.keriko.echorpc.server.tcp.VertxTcpServer;
import com.keriko.example.common.service.UserService;
import com.keriko.echorpc.RpcApplication;
import com.keriko.echorpc.config.RegistryConfig;
import com.keriko.echorpc.config.RpcConfig;
import com.keriko.echorpc.model.ServiceMetaInfo;
import com.keriko.echorpc.registry.EtcdRegistry;
import com.keriko.echorpc.registry.LocalRegistry;
import com.keriko.echorpc.registry.Registry;
import com.keriko.echorpc.registry.RegistryFactory;
import com.keriko.echorpc.server.HttpServer;
import com.keriko.echorpc.server.VertxHttpServer;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务提供者示例
 */
@Slf4j
public class ProviderExample {

    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        System.out.println("======");
//        System.out.println("======");
//        System.out.println("======");
//        System.out.println("======");
//        System.out.println("======");
        log.info("Registering serviceName: {}", serviceName);
        log.info("Registering serviceHost: {}", rpcConfig.getServerHost());
        log.info("Registering servicePort: {}", rpcConfig.getServerPort());
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动 web 服务
//        HttpServer httpServer = new VertxHttpServer();
//        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8080);
    }
}
