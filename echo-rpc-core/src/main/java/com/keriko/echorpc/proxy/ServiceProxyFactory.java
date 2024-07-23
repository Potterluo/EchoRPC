package com.keriko.echorpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂类，用于创建服务类的代理实例。
 * 代理工厂通过动态代理机制，在运行时生成代理类，代理类实现了指定的服务接口，可以在调用接口方法时添加额外的操作。
 */
public class ServiceProxyFactory {

    /**
     * 获取指定服务类的代理实例。
     * 该方法通过Java的动态代理机制，生成一个实现了指定接口的代理类。代理类在调用具体方法时，会委托给ServiceProxy的invoke方法处理。
     *
     * @param serviceClass 服务接口类，用于指定生成代理类所实现的接口。
     * @param <T> 泛型参数，表示服务接口的类型。
     * @return 返回一个代理实例，该实例实现了serviceClass指定的接口。
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        System.out.println("getProxy:"+serviceClass);
        // 使用Proxy.newProxyInstance创建代理实例，其中传入的服务类加载器、实现的接口列表和服务代理实例。
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}


