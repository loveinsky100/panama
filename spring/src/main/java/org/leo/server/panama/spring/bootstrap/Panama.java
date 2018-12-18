package org.leo.server.panama.spring.bootstrap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.leo.server.panama.core.handler.RequestHandler;
import org.leo.server.panama.server.Server;
import org.leo.server.panama.server.http.HttpServer;
import org.leo.server.panama.server.tcp.TCPServer;
import org.leo.server.panama.server.websocket.HttpWebSocketServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuyangze
 * @date 2018/10/8 下午1:29
 */
public class Panama {
    private final static Log log = LogFactory.getLog(Panama.class);

    private final static String PANAMA_SPRING_SCAN_PACKAGE = "org.leo.server.panama.spring.function.*";
    private final static String PANAMA_SPRING_DEFAULT_HTTP_HANDLER = "nettyRequestFunctionHttpHandler";
    private final static String PANAMA_SPRING_DEFAULT_WEB_SOCKET_HANDLER = "nettyRequestFunctionWebSocketHandler";
    private final static String PANAMA_SPRING_DEFAULT_TCP_HANDLER = "nettyRequestFunctionTcpHandler";

    private static ApplicationContext applicationContext;
    private static Map<String, Server> serverMap = new ConcurrentHashMap<>();

    public static void load(Object ...args) {
        log.info("panama server loading");
        Class mainClass = deduceMainApplicationClass();
        List<String> basePackageList = new ArrayList<>();
        ComponentScan componentScan = (ComponentScan)mainClass.getAnnotation(ComponentScan.class);
        if (null != componentScan) {
            for (String basePackage : componentScan.basePackages()) {
                if (basePackage.length() > 0) {
                    basePackageList.add(basePackage);
                }
            }
        }

        if (basePackageList.size() == 0) {
            basePackageList.add(mainClass.getPackage().getName() + ".*");
        }

        basePackageList.add(PANAMA_SPRING_SCAN_PACKAGE);
        applicationContext = new AnnotationConfigApplicationContext(basePackageList.toArray(new String[basePackageList.size()]));
        log.info("panama server loading success");
    }

    public static Server startHttpServer(int maxThread, int port) {
        return startHttpServer(maxThread, port, Panama.httpHandler());
    }

    public static Server startHttpServer(int maxThread, int port, RequestHandler requestHandler) {
        Server server = new HttpServer(port, requestHandler);
        Panama.startServer(maxThread, "httpServer", server);

        return server;
    }

    public static Server startTcpServer(int maxThread, int port) {
        return startTcpServer(maxThread, port, Panama.tcpHandler());
    }

    public static Server startTcpServer(int maxThread, int port, RequestHandler requestHandler) {
        Server server = new TCPServer(port, requestHandler);
        Panama.startServer(maxThread, "tcpServer", server);

        return server;
    }

    public static Server startWebSocketServer(int maxThread, int port) {
        Server server = new HttpWebSocketServer(port, Panama.httpHandler(), Panama.webSocketHandler());
        Panama.startServer(maxThread, "webSocketServer", server);

        return server;
    }

    public synchronized static void startServer(int maxThread, String serverName, Server server) {
        Server containServer = serverMap.get(serverName);
        if (null != containServer) {
            throw new RuntimeException(serverName + " already exist");
        }

        serverMap.put(serverName, server);
        Thread serverThread = new Thread(() -> {
            log.info("panama server start: " + serverName + " at port:" + server.port() + " maxThread: " + maxThread);
            server.start(maxThread);
        });

        serverThread.setName(serverName + " Server Thread");
        serverThread.start();
    }

    public synchronized static void shutdown(String serverName) throws Exception {
        Server server = serverMap.get(serverName);
        if (null != server) {
            server.shutdown().get();
        }
    }

    public static <T> T getBean(String beanName) {
        return (T)applicationContext.getBean(beanName);
    }

    public static RequestHandler httpHandler() {
        return getBean(PANAMA_SPRING_DEFAULT_HTTP_HANDLER);
    }

    public static RequestHandler webSocketHandler() {
        return getBean(PANAMA_SPRING_DEFAULT_WEB_SOCKET_HANDLER);
    }

    public static RequestHandler tcpHandler() {
        return getBean(PANAMA_SPRING_DEFAULT_TCP_HANDLER);
    }

    /**
     * 从栈里获取原始的main函数
     *
     * @return
     */
    private static Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            // Swallow and continue
        }

        return null;
    }
}
