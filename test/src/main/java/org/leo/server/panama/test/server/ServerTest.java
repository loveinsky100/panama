package org.leo.server.panama.test.server;

import org.leo.server.panama.spring.bootstrap.Panama;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author xuyangze
 * @date 2018/10/8 下午2:07
 */
@ComponentScan("org.leo.server.panama.test.server.function.*")
public class ServerTest {
    public static void main(String []args) {
        // load panama spring config
        Panama.load(args);

        // connect: http://localhost:8080
        // client send: http://localhost:8080/hello?name=Leo
        // server response: Leo
        Panama.startHttpServer(10, 8080);

        // connect: telnet localhost 8081
        // client send: hello?name=Leo
        // server response: Leo
        Panama.startTcpServer(10, 8081);

        // connect: ws://localhost:8082/ws?conn=1
        // client send message: hello?name=ANY_STRING
        // server response: ANY_STRING
        Panama.startWebSocketServer(10, 8082);
    }
}