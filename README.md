# EchoRPC

基于 Java + Etcd + Vert.x 的高性能RPC框架

# 1 概述

## 什么是RPC框架？

**RPC** （Remote Procedure Call，远程过程调用）框架是一种软件框架，允许程序在不同的地址空间（例如，在不同的机器上）执行彼此的方法或函数，仿佛它们是本地调用的一部分。RPC框架旨在简化分布式计算，使开发者可以更方便地构建和使用跨网络的服务。



**RPC调用示例**

点餐服务和接口的示例伪代码如下：

```java
interface OrderService {
    //点餐，返回orderID
    long oreder(参数1，参数2，参数3)
}
```

- HTTP请求调用

  ```java
  url = "https://lxjchina.com.cn/"
  req = new Req(参数1，参数2，参数3)
  res = httpClient.post(url).body(req).execute()
  orderId = res.data.orderId
  ```

- RPC调用

  ```java
  orderId = orderService.order(参数1，参数2，参数3)
  ```

  

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

# 2 简易RPC 框架的实现思路

## 2.1 简易RPC框架结构设计

考虑一下场景，消费者A想调用自身进程内点餐服务orderService接口中的order方法。

```java
orderService.order(args[]);
```

那orderService不是自身进程提供的呢？有一个餐馆服务商，提供了order方法

常规的调用方法：

```mermaid
flowchart LR
    消费者 --> 请求客户端\nHTTP/其他 --请求--> web服务器\nhttps://lxjchina.com.cn/ --> 服务提供者\norederService接口\norder方法

```

若服务提供者提供了多个服务和接口，针对每个接口都写一个HTTP调用接口及逻辑，过于复杂。

**解决方案：**

提供一个统一的服务调用接口，通过`请求处理器`根据客户端的请求参数来进行不同的处理，调用不同的服务和方法。

请求处理器如何知道该怎么调用方法呢？

可以在服务提供者程序维护一个`本地服务注册器`，记录服务和对应实现类的映射。

消费者要调用 `ordersenice` 服务的 `order`方法，可以发送清求，参数为 `service=orderservice,method=order` ，然后请求处理器会根据 `serice` 从服
务注册器中找到对应的服务实现类，并且通过 Java 的反射机制调用 `method` 指定的方法。

> **反射机制**是 Java 语言的一大重要特性，它使得程序可以在运行时动态地获取有关类和对象的信息，并能够调用对象的方法或访问对象的属性。通过反射机制，Java 能够在不用预先知道类的情况下处理类和对象。

由于Java对象无法直接在网路中传输，所以要对传输的参数进行序列化和反序列化。

![简易RPC框架结构示意图](https://pic.imgdb.cn/item/66821c15d9c307b7e92727db.png)

如上图所示，虚线框部分就是PRC框架所需要提供的基础功能。

## 2.2 RPC框架的简易实现

1. **easy-rpc框架**
```
EchoRPC 简易版框架
├── echo-rpc-easy
├── example-common
├── example-consumer
├── example-provider
```
   在example-common模块编写User实体类（需实现序列化接口，方便网络传输）和服务接口UserService（提供getUser（）方法，但不实现）

   服务提供者example-provider ，实现服务接口 UserServiceImpl

   服务消费者example-consumer，调用 `User newUser = userService.getUser(user);`

2. **Web服务器**

   需要让服务提供者提供**可远程访问**的服务，需要用到web服务器，能够接受处理请求，返回响应。

   本项目使用高性能NIO框架的Vert.x来作为RPC框架的web服务器。

   >**Tomcat**:  Tomcat 是一个Servlet容器，主要用于处理HTTP请求。如果需要一个高效的RPC框架，使用Tomcat显得过于笨重和不合适。Tomcat的同步阻塞模型不适合高性能RPC需求。
   >
   >**Netty**：高性能、异步事件驱动的网络应用框架，适用于构建高并发、高吞吐量的网络应用。就应用开发的角度来水，Vert.x的学习成本相对于Netty⽽⾔更少。

3. **本地注册服务器**

   因为消费者与提供者之间需要进⾏⽹络通讯的话，需要我们在注册中⼼ 获取到提供者注册的信息，那么消费者根据获取到的信息，再进⼀步进⾏⽹络通 讯，进⽽调⽤提供者给到的服务。

   简易框架就先使用本地存储来实现。

   ```java
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();
   ```

   > `ConcurrentHashMap` 是线程安全的，这意味着它可以在多线程环境中安全地进行读写操作而不需要额外的同步机制。对于RPC框架来说，注册中心服务很可能会被多个线程并发访问，例如服务注册和服务发现操作。因此，使用线程安全的数据结构非常重要。

4. **序列化器**

   序列化（Serialization）是将对象的状态转换为字节流的过程。这个过程使得对象可以被存储到磁盘或通过网络传输，并在需要的时候重新恢复为对象。

   反序列化（Deserialization）是将字节流转换回对象的过程。通过反序列化，可以将序列化的字节流恢复为原始的对象。

   对于Java来说，对象存在于JVM虚拟机中，如果需要将对象持久化存储/通过网络传输/深复制，就需要将对象转换为字节流。

   > - **Java内置Serializable**：简单易用，但适用于仅Java环境。
   > - **JSON（如Jackson或Gson）**：跨语言，文本格式，易读。
   > - **XML（如JAXB）**：跨语言，自描述，适合复杂数据结构。
   > - **Protocol Buffers**：高效、跨语言、严格数据结构。

   简易框架使用JDK内置序列化器。先写序列化接⼝，再写JDK序列化器，便于以后扩展。

5. **请求处理器(提供者接收到请求后的⽽处理)**

   当web服务器获取到请求的数据后，需要经过请求处理器进⾏处理。

   1. 将请求发送过来的字节数组数据进⾏反序列化为对象，便于后续的使⽤

      ```java
      byte[] bytes = body.getBytes();
      rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
      ```

   2. 因为提供者已经将服务注册到本地注册器，那么我们只需要使⽤反序列化得到 的对象中的服务名，通过get⽅法获取得到服务实现类

      ```java
      Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
      ```

   3. 通过反射的⽅式，进⾏实现类中的⽅法调⽤

      ```java
      Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                      Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
      ```

   4. 最后将得到的结果，通过响应类进⾏封装，最后再响应出去给调⽤者，当响应 出去的时候，同样也要⽤到序列化，可以理解为，在本地处理逻辑⽤对象，在 ⽹络传输之间⽤字节数组。

      ```java
      rpcResponse.setData(result);
      
      doResponse(request, rpcResponse, serializer);
      ```

6. **代理服务（消费方发起调用）**


- **静态代理：**
   构造HTTP请求去调用服务提供者。为每一个服务都提供一个实现类。灵活性较差，RPC框架中，常使用动态代理。

- **动态代理：** 
  动态代理的作用是，根据要生成的对象的类型，自动生成一个代理对象，常用的动态代理实现方式有 JDK动态代理和基于字节码生成的动态代理(比如CGLIB)。前者简单易用、无需引入额外的库，但缺点是只能对接口进行代理;后者更灵活、可以对任何类进行代理，但性能略低于JDK动态代理。此处使用 JDK 动态代理。

  

  逻辑：通过⼯⼚模式，填⼊要获取的类的代理，例如

  ```java
   UserService userService = ServiceProxyFactory.getProxy(UserService.class);
  ```

  ```java
   public static <T> T getProxy(Class<T> serviceClass) {   
  // 使用Proxy.newProxyInstance创建代理实例，其中传入的服务类加载器、实现的接口列表和服务代理实例。
          return (T) Proxy.newProxyInstance(
                  serviceClass.getClassLoader(),
                  new Class[]{serviceClass},
                  new ServiceProxy());
      }
  ```

  

  至此，一个简易的RPC框架就完成了。

  ![Provider](https://pic.imgdb.cn/item/66827833d9c307b7e9b61d8c.png)

  ![Consumer](https://pic.imgdb.cn/item/6682785ed9c307b7e9b66142.png)

  

