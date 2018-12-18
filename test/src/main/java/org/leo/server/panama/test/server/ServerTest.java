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
        Panama.load(args);
        Panama.startHttpServer(10, 8080);
        Panama.startTcpServer(10, 8081);
        Panama.startWebSocketServer(10, 8082);
    }
}