package com.keriko.echorpc.fault.retry;

import com.keriko.echorpc.model.RpcResponse;
import org.junit.Test;

/**
 * 重试策略测试
 */
public class RetryStrategyTest {

    RetryStrategy retryStrategy = new NoRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }

    RetryStrategy retryStrategy1 = new FixedIntervalRetryStrategy();
    @Test
    public void doRetry1() {
        try {
            RpcResponse rpcResponse = retryStrategy1.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }

    RetryStrategy retryStrategy2 = new ExponentialBackoffRetryStrategy();
    @Test
    public void doRetry2() {
        try {
            RpcResponse rpcResponse = retryStrategy2.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }

    RetryStrategy retryStrategy3 = new RandomIntervalRetryStrategy();
    @Test
    public void doRetry3() throws Exception {
        try {
            RpcResponse rpcResponse = retryStrategy3.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }


}
