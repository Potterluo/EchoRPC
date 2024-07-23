package com.keriko.echorpc.server;

/**
 * HttpServer接口定义了一个HTTP服务器的基本行为。
 * 该接口主要负责服务器的启动操作。
 */
public interface HttpServer {
    
    /**
     * 启动服务器。
     *
     * @param port 服务器监听的端口号。指定服务器将通过该端口接受HTTP请求。
     *             必须是一个有效的端口号（1-65535）。
     *             如果端口号已被其他服务占用，服务器启动可能会失败。
     */
    void doStart(int port);
}
