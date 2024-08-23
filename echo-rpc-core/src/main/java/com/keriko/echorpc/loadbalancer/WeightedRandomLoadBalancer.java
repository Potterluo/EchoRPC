package com.keriko.echorpc.loadbalancer;

import com.keriko.echorpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 加权随机负载均衡器
 */
public class WeightedRandomLoadBalancer implements LoadBalancer {

    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int totalWeight = 0;
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            totalWeight += serviceMetaInfo.getWeight();
        }

        if (totalWeight <= 0) {
            return null;
        }

        int randomWeight = random.nextInt(totalWeight);
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            randomWeight -= serviceMetaInfo.getWeight();
            if (randomWeight < 0) {
                return serviceMetaInfo;
            }
        }

        // 如果所有权重都为零，则随机选择一个
        return serviceMetaInfoList.get(random.nextInt(serviceMetaInfoList.size()));
    }
}