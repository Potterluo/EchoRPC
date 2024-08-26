package com.keriko.echorpc.fault.retry;

import com.github.rholder.retry.*;
import com.keriko.echorpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 指数退避 - 重试策略
 */
@Slf4j
public class ExponentialBackoffRetryStrategy implements RetryStrategy {

    /**
     * 使用重试机制执行RPC调用
     * 本方法通过Retryer框架实现，用于在指定条件下自动重试失败的RPC调用
     *
     * @param callable 实现了Callable接口的对象，包含RPC调用逻辑
     * @return RpcResponse RPC调用的响应结果
     * @throws ExecutionException 如果最终尝试仍失败，则抛出此异常
     * @throws RetryException 如果重试流程中出现异常，则可能抛出此异常
     */
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        // 构建一个重试器实例，用于处理RPC调用的重试逻辑
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                // 对于抛出Exception类型异常的调用进行重试
                .retryIfExceptionOfType(Exception.class)
                // 设置等待策略为指数退避，初始等待时间为1秒，最大等待时间为30秒，退避因子为2
                .withWaitStrategy(WaitStrategies.exponentialWait(1000L, 30000L, TimeUnit.MILLISECONDS))
                // 设置停止策略为最多尝试5次
                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                // 添加重试监听器，用于在每次重试时打印重试次数和等待时间
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        if (attempt.hasException()) {
                            log.info("重试次数 {}, 等待时间 {} ms", attempt.getAttemptNumber(), attempt.getDelaySinceFirstAttempt());
                        }
                    }
                })
                .build();
        // 使用重试器执行RPC调用，并返回最终的调用结果
        return retryer.call(callable);
    }
}