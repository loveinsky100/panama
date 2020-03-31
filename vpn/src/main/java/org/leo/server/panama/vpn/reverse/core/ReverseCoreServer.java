package org.leo.server.panama.vpn.reverse.core;

import com.google.common.cache.Cache;
import io.netty.channel.*;
import org.apache.log4j.Logger;
import org.leo.server.panama.core.connector.impl.TCPRequest;
import org.leo.server.panama.core.handler.RequestHandler;
import org.leo.server.panama.core.handler.tcp.TCPRequestHandler;
import org.leo.server.panama.server.tcp.TCPServer;
import org.leo.server.panama.util.NumberUtils;
import org.leo.server.panama.vpn.reverse.constant.ReverseConstants;
import org.leo.server.panama.vpn.reverse.protocol.ReverseProtocol;
import org.leo.server.panama.vpn.util.Callback;
import org.leo.server.panama.vpn.util.LocalCacheFactory;
import org.leo.server.panama.vpn.util.MD5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 反向代理服务端，作为服务端存在外网服务器中
 * 内网客户端连接到此端口，并维持一条稳定的连接
 * 代理服务器请求后发起网络请求，然后回调给代理服务器
 * 由于复用一条连接，所有的请求带上tag标记，判断是否属于同一个请求
 * @author xuyangze
 * @date 2018/11/21 3:17 PM
 */
public class ReverseCoreServer extends TCPServer implements RequestHandler<TCPRequest> {
    private final static Logger log = Logger.getLogger(ReverseCoreServer.class);

    private RandomList<Channel> channels = new RandomList();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Cache<Integer, Consumer<byte []>> tag2ConsumerMap = LocalCacheFactory.createCache(60 * 1000 * 5, 20000);
    private Cache<Integer, Callback> tag2ClosedMap = LocalCacheFactory.createCache(60 * 1000 * 5, 20000);

    private ReverseProtocol.ReverseProtocolData lastUnCompleteReverseProtocolData;

    public ReverseCoreServer(int port) {
        super(port);
    }

    public void send2Client(int tag, byte []data, Consumer<byte []> callback, Callback closed) {
        // 获取一条连接
        Channel channel = read(() -> channels.select());
        if (null == channel) {
            log.error("proxy --------!-------> target: 0 inner found");
            return;
        }

        // 生成tag
        if (null != callback) {
            tag2ConsumerMap.put(tag, callback);
        }

        if (null != closed) {
            tag2ClosedMap.put(tag, closed);
        }

        // 由于复用同一条连接，所有的请求应当带上标记
        channel.writeAndFlush(ReverseProtocol.encodeProtocol(tag, data));
    }

    @Override
    protected void setupPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new TCPRequestHandler(this));
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        log.info("a new client connected to reverse server");
        write(() -> channels.add(ctx.channel()));
    }

    @Override
    public void doRequest(TCPRequest request) {
        List<ReverseProtocol.ReverseProtocolData> reverseProtocolDatas = ReverseProtocol.decodeProtocol(request.getData(), lastUnCompleteReverseProtocolData);
        lastUnCompleteReverseProtocolData = null;

        if (null == reverseProtocolDatas || reverseProtocolDatas.size() == 0) {
            log.error("reverseProtocolDatas is empty, but data size is: " + request.getData().length);
            return;
        }

        reverseProtocolDatas.forEach(reverseProtocolData -> this.doRequest(reverseProtocolData));
    }

    @Override
    public void onClose(ChannelHandlerContext ctx) {
        write(() -> channels.remove(ctx.channel()));
        for (Callback callback : tag2ClosedMap.asMap().values()) {
            callback.call();
        }

        lastUnCompleteReverseProtocolData = null;
        tag2ClosedMap.invalidateAll();
        tag2ConsumerMap.invalidateAll();
    }

    private void doRequest(ReverseProtocol.ReverseProtocolData reverseProtocolData) {
        if (!reverseProtocolData.isComplete()) {
            lastUnCompleteReverseProtocolData = reverseProtocolData;
            return;
        }

        int tag = reverseProtocolData.getTag();
        byte[] data = reverseProtocolData.getData();
        Consumer<byte []> consumer = tag2ConsumerMap.getIfPresent(tag);
        if (null != consumer) {
            if (data.length != 4) {
                consumer.accept(data);
            } else {
                int closeFlag = NumberUtils.byteArrayToInt(data);
                if (closeFlag == ReverseConstants.CLOSE_MAGIC) {
                    Callback callback = tag2ClosedMap.getIfPresent(tag);
                    if (null != callback) {
                        tag2ConsumerMap.invalidate(tag);
                        tag2ClosedMap.invalidate(tag);
                        callback.call();
                    }
                } else {
                    consumer.accept(data);
                }
            }
        }
    }

    private <T> T read(Supplier<T> supplier) {
        try {
            readWriteLock.readLock().lock();
            return supplier.get();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private <T> T write(Supplier<T> supplier) {
        try {
            readWriteLock.writeLock().lock();
            return supplier.get();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public class RandomList<T> extends ArrayList<T> {
        private Random rand = new Random(47);

        public T select() {
            if (this.size() > 0) {
                return get(0);
            }

            return null;
        }
    }
}
