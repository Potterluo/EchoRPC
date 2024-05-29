# EchoRPC

基于 Java + Etcd + Vert.x 的高性能RPC框架

## 什么是RPC框架？

**RPC** （Remote Procedure Call，远程过程调用）框架是一种软件框架，允许程序在不同的地址空间（例如，在不同的机器上）执行彼此的方法或函数，仿佛它们是本地调用的一部分。RPC框架旨在简化分布式计算，使开发者可以更方便地构建和使用跨网络的服务。

## 常见的RPC框架

**gRPC**：

- 由Google开发，基于HTTP/2和Protocol Buffers。
- 支持多种编程语言。
- 高性能、双向流、负载均衡等特性。

**Thrift**：

- 由Facebook开发，支持多种编程语言。
- 提供接口定义语言（IDL）来定义服务和数据类型。

**Apache Dubbo**：

- 一个高性能的Java RPC框架，广泛用于阿里巴巴的服务。
- 支持多种协议和序列化方式，具有服务治理功能。

**XML-RPC**：

- 基于XML格式的简单RPC协议。
- 通过HTTP进行通信，较为简单和易于理解。

**JSON-RPC**：

- 基于JSON格式的RPC协议。
- 通常通过HTTP或WebSocket进行通信，较为轻量和易于集成。

# 2 RPC 框架的实现思路

1. 考虑一下场景，消费者A想调用自身进程内点餐服务orderService接口中的order方法。

   ```java
   orderService.order(args[]);
   ```

   那orderService不是自身进程提供的呢？有一个餐馆服务商，提供了order方法

   常规的调用方法：

   

   

