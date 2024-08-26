package com.keriko.echorpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.keriko.echorpc.RpcApplication;
import com.keriko.echorpc.config.RpcConfig;
import com.keriko.echorpc.constant.RpcConstant;
import com.keriko.echorpc.fault.retry.RetryStrategy;
import com.keriko.echorpc.fault.retry.RetryStrategyFactory;
import com.keriko.echorpc.loadbalancer.LoadBalancer;
import com.keriko.echorpc.loadbalancer.LoadBalancerFactory;
import com.keriko.echorpc.model.RpcRequest;
import com.keriko.echorpc.model.RpcResponse;
import com.keriko.echorpc.model.ServiceMetaInfo;
import com.keriko.echorpc.protocol.*;
import com.keriko.echorpc.registry.Registry;
import com.keriko.echorpc.registry.RegistryFactory;
import com.keriko.echorpc.serializer.Serializer;
import com.keriko.echorpc.serializer.SerializerFactory;
import com.keriko.echorpc.server.tcp.VertxTcpClient;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 服务代理（JDK 动态代理）
 *
 */
public class ServiceProxy implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(ServiceProxy.class);

    /**
     * 调用代理
     *
     * @return 调用结果对象
     * @throws Throwable 如果调用过程中发生错误，则抛出异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            log.info("服务发现: {}", serviceMetaInfoList);
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                log.error("暂无服务地址");
                throw new RuntimeException("暂无服务地址");
            }

            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            // 将调用方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            // rpc 请求
            // 使用重试机制
            log.info("开始调用服务: {}", selectedServiceMetaInfo);
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
            return rpcResponse.getData();
        } catch (Exception e) {
            return null;
        }
    }

}
