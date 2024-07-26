package com.keriko.echorpc.registry;

import com.keriko.echorpc.spi.SpiLoader;


/**
 * 注册中心工厂类，负责创建并提供注册中心的实例。
 * 该类利用SPI（Service Provider Interface）机制，在静态块中加载所有的注册中心实现类，
 * 以便后续可以通过关键标识符动态获取注册中心实例。
 */
public class RegistryFactory {

    /**
     * 静态初始化块，在类加载时执行，用于加载所有的注册中心实现。
     * 这里使用了SpiLoader工具类，它基于Java的SPI机制，可以动态加载实现了Registry接口的类。
     */
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认的注册中心实例，这里使用EtcdRegistry作为默认实现。
     * 这种方式便于在没有指定特定注册中心时，提供一个默认的选择。
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 根据提供的关键标识符，获取对应的注册中心实例。
     * 如果关键标识符为空或无法找到对应的注册中心实现，将返回默认的注册中心实例。
     *
     * @param key 注册中心的标识符，用于唯一标识一个注册中心实现。
     * @return 对应的注册中心实例。
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }

}
