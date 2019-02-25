package org.leo.server.panama.vpn.reverse.server;

import com.google.common.collect.Maps;
import io.netty.buffer.Unpooled;
import org.apache.log4j.Logger;
import org.leo.server.panama.server.Server;
import org.leo.server.panama.util.NumberUtils;
import org.leo.server.panama.vpn.proxy.Proxy;
import org.leo.server.panama.vpn.proxy.factory.ShadowSocksProxyFactory;
import org.leo.server.panama.vpn.proxy.impl.ReverseShadowSocksProxy;
import org.leo.server.panama.vpn.reverse.constant.ReverseConstants;
import org.leo.server.panama.vpn.reverse.core.ReverseCoreClient;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 反向代理客户端，发送请求到代理端，代理端返回数据
 * 服务端向被代理端进行长链接，服务端维持和代理端的连接
 * 代理端收到客户端请求，通过长链接发送请求到服务端
 * 服务端转发请求到服务端的ss端口，请求返回后返回给代理端
 * @author xuyangze
 * @date 2018/11/21 3:17 PM
 */
public class ReverseShadowSocksServer implements Server {
    private final static Logger log = Logger.getLogger(ReverseShadowSocksServer.class);

    private ReverseCoreClient reverseCoreClient;
    private int port;
    private String host;

    private Map<Integer, Proxy> tag2Proxy = Maps.newConcurrentMap();

    public ReverseShadowSocksServer(String host, int port) {
        this.port = port;
        this.host = host;
        this.reverseCoreClient = new ReverseCoreClient(new InetSocketAddress(host, port), this::doRequest);
    }

    @Override
    public void start(int maxThread) {
        this.reverseCoreClient.connect(new InetSocketAddress(host, port));
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public Future shutdown() {
        return null;
    }

    private void doRequest(byte []data) {
        int tag = NumberUtils.byteArrayToInt(data);
        byte []realData = new byte[data.length - 4];
        System.arraycopy(data, 4, realData, 0, realData.length);
        Proxy proxy = tag2Proxy.get(tag);
        if (null == proxy) {
            proxy = ShadowSocksProxyFactory.create(reverseCoreClient.channel(), () -> {
                // 发送关闭请求
                sendCloseMsg(tag);
            });

            if (proxy instanceof ReverseShadowSocksProxy) {
                ReverseShadowSocksProxy reverseShadowSocksProxy = (ReverseShadowSocksProxy)proxy;
                reverseShadowSocksProxy.setAppendTagFunc(response -> Unpooled.wrappedBuffer(NumberUtils.intToByteArray(tag), response));
            }

            tag2Proxy.put(tag, proxy);
        }

        try {
            proxy.doProxy(realData);
        } catch (Exception e) {
            log.error("doRequest error", e);
            sendCloseMsg(tag);
        }
    }

    private void sendCloseMsg(int tag) {
        // 发送关闭请求
        reverseCoreClient.channel().writeAndFlush(Unpooled.wrappedBuffer(NumberUtils.intToByteArray(tag), NumberUtils.intToByteArray(ReverseConstants.CLOSE_MAGIC)));
        tag2Proxy.remove(tag);
    }

}
