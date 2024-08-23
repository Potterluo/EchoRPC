package com.keriko.echorpc.loadbalancer;

import com.keriko.echorpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 加权轮询负载均衡器
 */
public class WeightedRoundRobinLoadBalancer implements LoadBalancer {

    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        int totalWeight = 0;
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            totalWeight += serviceMetaInfo.getWeight();
        }

        if (totalWeight <= 0) {
            return null;
        }

        int currentWeight = currentIndex.getAndIncrement() % totalWeight;
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            currentWeight -= serviceMetaInfo.getWeight();
            if (currentWeight < 0) {
                return serviceMetaInfo;
            }
        }

        // 如果所有权重都为零，则轮询选择一个
        int size = serviceMetaInfoList.size();
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }
}