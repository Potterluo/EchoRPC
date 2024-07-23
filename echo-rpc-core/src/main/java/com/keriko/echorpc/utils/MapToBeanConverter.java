package com.keriko.echorpc.utils;

import java.lang.reflect.Field;
import java.util.Map;
import com.keriko.echorpc.config.RpcConfig;

public class MapToBeanConverter {

    public static <T> T convertMapToBean(Map<String, Object> map, Class<T> tClass, String prefix) {
        try {
            T instance = tClass.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (prefix != null && !key.startsWith(prefix)) {
                    continue;
                }
                String fieldName = key.substring(prefix.length());
                Field field = tClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(instance, entry.getValue());
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert map to bean", e);
        }
    }

    public static void main(String[] args) {
        Map<String, Object> map = Map.of(
                "rpc_name", "echo-rpc",
                "rpc_version", "2.0",
                "rpc_serverHost", "localhost",
                "rpc_serverPort", 8080
        );


        RpcConfig config = convertMapToBean(map, RpcConfig.class, "rpc_");
        System.out.println(config);
    }
}

