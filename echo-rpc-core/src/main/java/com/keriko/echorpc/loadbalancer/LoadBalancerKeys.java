package com.keriko.echorpc.loadbalancer;

/**
 * 负载均衡器键名常量
 */
public interface LoadBalancerKeys {

    /**
     * 轮询
     */
    String ROUND_ROBIN = "roundRobin";

    /**
     * 随机
     */
    String RANDOM = "random";

    /**
     * 加权轮询
     */
    String WEIGHTED_ROUND_ROBIN = "weightedRoundRobin";

    /**
     * 加权随机
     */
    String WEIGHTED_RANDOM = "weightedRandom";

    /**
     * 一致性哈希
     */
    String CONSISTENT_HASH = "consistentHash";

}
