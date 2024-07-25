package com.keriko.echorpc.serializer;

import com.keriko.echorpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * 序列化器工厂（工厂模式，用于获取序列化器对象）
 */
@Slf4j
public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        if (key == null) {
            return DEFAULT_SERIALIZER;
        }
        log.info("get serializer by key: {}", key);
        return SpiLoader.getInstance(Serializer.class, key);
    }
    public static void main(String[] args) {
        SerializerFactory.getInstance("hessian");
    }

}
