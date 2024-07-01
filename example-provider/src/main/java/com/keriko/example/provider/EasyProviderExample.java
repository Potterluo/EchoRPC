package com.keriko.example.provider;


import com.keriko.echorpc.registry.LocalRegistry;
import com.keriko.echorpc.server.HttpServer;
import com.keriko.echorpc.server.VertxHttpServer;
import com.keriko.example.common.service.UserService;

/**
 * EasyProviderExample类是一个简单的示例类，不提供具体的业务逻辑功能。
 * 它包含了一个main方法作为程序的入口点。
 */
public class EasyProviderExample {

    /**
     * 程序的主入口函数。此函数为程序的起始点，当程序运行时首先执行此函数。
     * 主要进行了两步操作：注册服务和启动HTTP服务器。
     * @param args 命令行传入的参数数组。这里没有对传入的参数进行处理，但通常可以用来对程序的行为进行配置。
     */
    public static void main(String[] args) {
        // 注册UserService服务的实现类到本地注册表
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 创建并启动VertxHTTP服务器，监听8080端口
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }

}

