package org.leo.server.panama.vpn.reverse.server;

import com.google.common.cache.Cache;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.SucceededFuture;
import org.apache.log4j.Logger;
import org.leo.server.panama.server.Server;
import org.leo.server.panama.util.NumberUtils;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.proxy.Proxy;
import org.leo.server.panama.vpn.proxy.factory.ShadowSocksProxyFactory;
import org.leo.server.panama.vpn.proxy.impl.RePlayShadowSocksProxy;
import org.leo.server.panama.vpn.proxy.impl.ReverseShadowSocksProxy;
import org.leo.server.panama.vpn.reverse.constant.ReverseConstants;
import org.leo.server.panama.vpn.reverse.core.ReverseCoreClient;
import org.leo.server.panama.vpn.reverse.protocol.ReverseProtocol;
import org.leo.server.panama.vpn.util.Callback;
import org.leo.server.panama.vpn.util.LocalCacheFactory;
import org.leo.server.panama.vpn.util.MD5;

import java.net.InetSocketAddress;
import java.util.List;
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

    private ShadowSocksConfiguration shadowSocksConfiguration;

    private ReverseProtocol.ReverseProtocolData lastUnCompleteReverseProtocolData;

    // TCP连接缓存。关闭连接时进行清理
    private Cache<Integer, Proxy> tag2Proxy = LocalCacheFactory.createCache(60 * 1000 * 5, 20000);

    /**
     * 配置信息
     * @param shadowSocksConfiguration
     */
    public ReverseShadowSocksServer(ShadowSocksConfiguration shadowSocksConfiguration) {
        this.port = shadowSocksConfiguration.getReversePort();
        this.host = shadowSocksConfiguration.getReverseHost();
        this.shadowSocksConfiguration = shadowSocksConfiguration;
        this.reverseCoreClient = new ReverseCoreClient(new InetSocketAddress(host, port), this::doRequest);
    }

    @Override
    public void start(int maxThread) {
        // 连接到外网服务器，reverseCoreClient将在断开连接口自动重连
        this.reverseCoreClient.connect(new InetSocketAddress(host, port));
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public Future shutdown() {
        this.reverseCoreClient.close();
        return null;
    }

    protected Proxy createProxy(int tag) {
        Proxy proxy = ShadowSocksProxyFactory.createReverseShadowSocksProxy(reverseCoreClient.channel(), () -> {
            // 发送关闭请求
//            tag2Proxy.invalidate(tag);
            sendCloseMsg(tag);
        }, shadowSocksConfiguration);

        if (proxy instanceof ReverseShadowSocksProxy) {
            ReverseShadowSocksProxy reverseShadowSocksProxy = (ReverseShadowSocksProxy)proxy;
            reverseShadowSocksProxy.setAppendTagFunc(response -> Unpooled.wrappedBuffer(ReverseProtocol.encodeProtocol(tag, response)));
        }

        if (proxy instanceof RePlayShadowSocksProxy) {
            RePlayShadowSocksProxy rePlayShadowSocksProxy = (RePlayShadowSocksProxy)proxy;
            rePlayShadowSocksProxy.setAppendTagFunc(response -> Unpooled.wrappedBuffer(ReverseProtocol.encodeProtocol(tag, response)));
        }

        return proxy;
    }

    /**
     * 接收到来自外网服务器的数据，信息如下
     * @param data
     */
    private void doRequest(byte []data) {
        List<ReverseProtocol.ReverseProtocolData> reverseProtocolDatas = ReverseProtocol.decodeProtocol(data, lastUnCompleteReverseProtocolData);
        lastUnCompleteReverseProtocolData = null;

        if (null == reverseProtocolDatas || reverseProtocolDatas.size() == 0) {
            log.error("reverseProtocolDatas is empty, but data size is: " + data.length);
            return;
        }

        reverseProtocolDatas.forEach(reverseProtocolData -> this.doProxy(reverseProtocolData));
    }

    private void doProxy(ReverseProtocol.ReverseProtocolData reverseProtocolData) {
        if (!reverseProtocolData.isComplete()) {
            lastUnCompleteReverseProtocolData = reverseProtocolData;
            return;
        }

        int tag = reverseProtocolData.getTag();
        byte []data = reverseProtocolData.getData();
        if (data.length == 4) {
            // 关闭连接
            int closeFlag = NumberUtils.byteArrayToInt(data);
            if (closeFlag == ReverseConstants.CLOSE_MAGIC) {
                Proxy proxy = tag2Proxy.getIfPresent(tag);
                if (null != proxy && proxy instanceof ReverseShadowSocksProxy) {
                    ReverseShadowSocksProxy reverseShadowSocksProxy = (ReverseShadowSocksProxy)proxy;
                    reverseShadowSocksProxy.closeTargetConnection();
                }

                tag2Proxy.invalidate(tag);
                return;
            }
        }

        Proxy proxy = tag2Proxy.getIfPresent(tag);
        if (null == proxy) {
            proxy = createProxy(tag);

            Proxy proxy2 = tag2Proxy.getIfPresent(tag);
            if (null != proxy2) {
                proxy = proxy2;
            }

            tag2Proxy.put(tag, proxy);
        }

        try {
            proxy.doProxy(data);
        } catch (Exception e) {
//            System.out.println("doRequest error for tag: " + tag);
            log.error("doRequest error for tag: " + tag, e);
            sendCloseMsg(tag);
        }
    }

    private void sendCloseMsg(int tag) {
        // 发送关闭请求
        System.out.println("send close to tag : " + tag);
        reverseCoreClient.channel().writeAndFlush(ReverseProtocol.encodeProtocol(tag, NumberUtils.intToByteArray(ReverseConstants.CLOSE_MAGIC)));
    }
}
