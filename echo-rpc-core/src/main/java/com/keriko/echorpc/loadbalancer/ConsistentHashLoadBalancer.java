package com.keriko.echorpc.loadbalancer;

import com.keriko.echorpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConsistentHashLoadBalancer implements LoadBalancer {
    private final ConcurrentSkipListMap<Integer, ServiceMetaInfo> virtualNodes = new ConcurrentSkipListMap<>();
    private static final int VIRTUAL_NODE_NUM = 100;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList == null || serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 确保在多线程环境下更新 virtualNodes 是线程安全的
        lock.lock();
        try {
            // 重新构建 virtualNodes
            virtualNodes.clear();
            for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
                for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                    String virtualNodeName = serviceMetaInfo.getServiceName() + "-" + i;
                    int hash = getHash(virtualNodeName);
                    virtualNodes.put(hash, serviceMetaInfo);
                }
            }

            int hash = getHash(requestParams.toString());
            Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
            if (entry == null) {
                entry = virtualNodes.firstEntry();
            }
            return entry != null ? entry.getValue() : null;
        } finally {
            lock.unlock();
        }
    }

    private int getHash(Object key) {
        // 使用更稳定的哈希算法
        return MurmurHash3.hash(key.toString());
    }
}

// 添加 MurmurHash3 类实现
class MurmurHash3 {
    /**
     * 使用MurmurHash3算法计算字符串的哈希值
     * 这种哈希函数对于字符串数据非常有效，可以生成更均匀分布的哈希值
     *
     * @param key 要计算哈希值的字符串
     * @return 计算得到的哈希值
     */
    public static int hash(String key) {
    // MurmurHash3常量
    int m = 0x5bd1e995;
    int r = 24;
    // 初始化哈希值
    int h = 0;
    // 字符串长度
    int len = key.length();
    // 计算字符串可以分为多少个4字节块
    int nblocks = len / 4;

    // 遍历4字节块
    for (int i = 0; i < nblocks; i++) {
        // 读取下一个4字节块，并将其组合成一个整数
        int k = key.charAt(i * 4) & 0xff;
        k |= ((key.charAt(i * 4 + 1) & 0xff) << 8);
        k |= ((key.charAt(i * 4 + 2) & 0xff) << 16);
        k |= ((key.charAt(i * 4 + 3) & 0xff) << 24);

        // 执行MurmurHash3的混洗操作
        k *= m;
        k ^= k >>> r;
        k *= m;

        // 更新哈希值
        h *= m;
        h ^= k;
    }

    // 处理剩余的字节
    int tail = 0;
    switch (len % 4) {
        case 3:
            tail = (key.charAt(len - 3) & 0xff) << 16;
        case 2:
            tail |= (key.charAt(len - 2) & 0xff) << 8;
        case 1:
            tail |= (key.charAt(len - 1) & 0xff);
            tail *= m;
            h ^= tail;
            break;
        default:
            // 如果没有剩余字节，则不做任何操作
            break;
    }

    // 最后的混洗操作，以提高随机性
    h ^= h >>> 13;
    h *= m;
    h ^= h >>> 15;

    // 返回最终的哈希值
    return h;
}


    public static void main(String[] args) {
        String key = "Hello, EchoRPC!";
        int hash = MurmurHash3.hash(key);
        System.out.println("Hash for key '" + key + "': " + hash);
    }

}
