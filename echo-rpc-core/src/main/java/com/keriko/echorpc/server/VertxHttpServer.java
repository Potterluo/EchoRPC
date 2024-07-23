package com.keriko.echorpc.server;

import io.vertx.core.Vertx;
/**
 * VertxHttpServer 类实现了 HttpServer 接口，用于创建并启动一个 Vert.x HTTP 服务器。
 */
public class VertxHttpServer implements HttpServer {

    /**
     * 启动 HTTP 服务器，监听指定端口。
     *
     * @param port 服务器监听的端口号。
     */
    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例，它是 Vert.x 应用程序的核心对象
        Vertx vertx = Vertx.vertx();

        // 使用 Vert.x 实例创建一个 HTTP 服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 监听端口并处理请求
        server.requestHandler(new HttpServerHandler());

        // 启动 HTTP 服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.err.println("Failed to start server: " + result.cause());
            }
        });
    }
}
