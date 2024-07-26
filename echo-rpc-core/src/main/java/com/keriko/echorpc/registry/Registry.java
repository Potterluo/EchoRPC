package com.keriko.echorpc.registry;

import com.keriko.echorpc.config.RegistryConfig;
import com.keriko.echorpc.model.ServiceMetaInfo;
import java.util.List;


/**
 * 注册中心接口，定义了服务注册与发现的相关操作。
 * 用于管理服务提供者和服务消费者之间的通信细节。
 */
public interface Registry {

    /**
     * 初始化注册中心，配置注册中心的相关参数。
     *
     * @param registryConfig 注册中心的配置信息，包含了注册中心的地址、端口等关键信息。
     */
    void init(RegistryConfig registryConfig);

    /**
     * 将服务提供者注册到注册中心。
     *
     * @param serviceMetaInfo 服务的元数据信息，包含了服务的名称、地址、端口等信息。
     * @throws Exception 如果注册过程中出现错误，则抛出异常。
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 从注册中心注销服务提供者。
     *
     * @param serviceMetaInfo 服务的元数据信息，包含了需要注销的服务的名称、地址、端口等信息。
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 从注册中心发现指定服务的所有提供者。
     *
     * @param serviceKey 服务的标识，用于查找和定位服务。
     * @return 返回一个包含所有服务提供者元数据信息的列表。
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 销毁注册中心，释放资源。
     * 通常在应用停止时调用，用于清理注册中心的资源，确保资源的正确回收。
     */
    void destroy();

}

