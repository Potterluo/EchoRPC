package com.keriko.echorpc.registry;

import com.keriko.echorpc.serializer.Serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 * @author <a href="http://keriko.fun">duxiaolong</a>
 */
public class LocalRegistry {
    /**
     * 存储注册信息的并发哈希映射
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册指定服务到本地注册表
     *
     * @param serviceName 服务名称，作为映射的键
     * @param implClass 服务的实现类，作为映射的值
     */
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }

    /**
     * 根据服务名称从本地注册表获取服务实现类
     *
     * @param serviceName 服务名称，查找的键
     * @return 返回与服务名称对应的服务实现类，如果不存在，则返回null
     */
    public static Class<?> get(String serviceName) {
        System.out.println("get serviceName: " + serviceName);
        return map.get(serviceName);
    }

    /**
     * 从本地注册表中移除指定服务
     *
     * @param serviceName 需要移除的服务名称
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }

}

