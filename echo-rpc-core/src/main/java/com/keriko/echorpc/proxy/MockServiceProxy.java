package com.keriko.echorpc.proxy;

import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理（JDK 动态代理）
 *
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {


    /**
     * 调用代理对象的方法。
     * 当通过代理对象调用实际方法时，此方法将被触发。它首先记录方法的调用信息，
     * 然后返回一个默认对象，该对象的类型与调用方法的返回类型匹配。
     * 这种方式常用于模拟或测试场景， where 无需实际执行方法逻辑，
     * 但需要返回一个合法的对象以供后续处理。
     *
     * @param proxy 代理对象，即调用方法的对象。
     * @param method 被调用的方法。
     * @param args 方法的参数数组。
     * @return 返回一个与方法返回类型匹配的默认对象。
     * @throws Throwable 如果方法执行过程中抛出异常，则抛出。
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 记录方法调用信息
        // 根据方法的返回值类型，生成特定的默认值对象
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());

        // 根据方法的返回类型返回一个默认对象
        return getDefaultObject(methodReturnType);
    }


    /**
     * 生成指定类型的默认值对象。对于基本类型，返回对应的默认值；对于引用类型，返回null。
     * 使用时，请确保类型参数非null，或在调用处捕获可能的NullPointerException。
     *
     * @param type 类型参数，不能为null。
     * @return 指定类型默认值的对象表示。
     */
    private Object getDefaultObject(Class<?> type) {
        // 增加非空检查
        if (type == null) {
            throw new IllegalArgumentException("Type parameter cannot be null.");
        }

        // 基本类型处理
        if (type.isPrimitive()) {
            // 基本类型处理
            if (type == boolean.class) {
                return false;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            } else if (type == byte.class) {
                return (byte) 0;
            } else if (type == char.class) {
                return (char) 0;
            } else if (type == float.class) {
                return 0.0f;
            } else if (type == double.class) {
                return 0.0;
            }
        }
        // 引用类型处理
        return null;
    }

}

