package com.keriko.echorpc.server.tcp;

import com.keriko.echorpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxTcpServer implements HttpServer {

    private static final Logger log = LoggerFactory.getLogger(VertxTcpServer.class);

//    private byte[] handleRequest(byte[] requestData) {
//        // 在这里编写处理请求的逻辑，根据 requestData 构造响应数据并返回
//        // 这里只是一个示例，实际逻辑需要根据具体的业务需求来实现
//        return "Hello, client!".getBytes();
//    }

    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 TCP 服务器
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(new TcpServerHandler());

        // 启动 TCP 服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("TCP server started on port " + port);
            } else {
                log.info("Failed to start TCP server: " + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
