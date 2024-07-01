package com.keriko.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.keriko.echorpc.model.RpcRequest;
import com.keriko.echorpc.model.RpcResponse;
import com.keriko.echorpc.serializer.JdkSerializer;
import com.keriko.echorpc.serializer.Serializer;
import com.keriko.example.common.model.User;
import com.keriko.example.common.service.UserService;

import java.io.IOException;

/**
 * 静态代理类
 * UserServiceProxy类实现了UserService接口，提供获取用户信息的服务。
 */
public class UserServiceProxy implements UserService {

    /**
     * 根据提供的用户对象获取用户信息。
     *
     * @param user 需要获取信息的用户对象。
     * @return 返回对应用户的信息。
     */
    @Override
    public User getUser(User user) {
        // 使用JDK序列化器进行序列化
        Serializer serializer = new JdkSerializer();

        // 构建RPC请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[] { User.class })
                .args(new Object[] { user })
                .build();
        try {
            // 序列化rpcRequest为字节码，准备发送
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // 向服务端发送请求并获取响应
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()){
                result = httpResponse.bodyBytes();
            }

            // 反序列化响应结果
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);

            // 返回响应中的用户信息
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            // 将IO异常转换为运行时异常抛出
            throw new RuntimeException(e);
        }
    }
}

