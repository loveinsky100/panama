package org.leo.server.panama.vpn.reverse.core;

import com.google.common.collect.Maps;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.apache.log4j.Logger;
import org.leo.server.panama.core.connector.impl.TCPRequest;
import org.leo.server.panama.core.handler.RequestHandler;
import org.leo.server.panama.core.handler.tcp.TCPRequestHandler;
import org.leo.server.panama.server.tcp.TCPServer;
import org.leo.server.panama.util.NumberUtils;
import org.leo.server.panama.vpn.reverse.constant.ReverseConstants;
import org.leo.server.panama.vpn.util.Callback;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 反向代理客户端，发送请求到代理端，代理端返回数据
 * 服务端向被代理端进行长链接，服务端维持和代理端的连接
 * 代理端收到客户端请求，通过长链接发送请求到服务端
 * 服务端转发请求到服务端的ss端口，请求返回后返回给代理端
 * @author xuyangze
 * @date 2018/11/21 3:17 PM
 */
public class ReverseCoreServer extends TCPServer implements RequestHandler<TCPRequest> {
    private final static Logger log = Logger.getLogger(ReverseCoreServer.class);

    private RandomList<Channel> channels = new RandomList();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Map<Integer, Consumer<byte []>> tag2ConsumerMap = Maps.newConcurrentMap();
    private Map<Integer, Callback> tag2ClosedMap = Maps.newConcurrentMap();

    public ReverseCoreServer(int port) {
        super(port);
    }

    public void send2Client(int tag, byte []data, Consumer<byte []> callback, Callback closed) {
        // 获取一条连接
        Channel channel = read(() -> channels.select());
        if (null == channel) {
            return;
        }

        // 生成flag
        byte []tagByte = NumberUtils.intToByteArray(tag);

        tag2ConsumerMap.put(tag, callback);
        tag2ClosedMap.put(tag, closed);

        // 由于复用同一条连接，所有的请求应当带上标记
        channel.writeAndFlush(Unpooled.wrappedBuffer(tagByte, data));
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
        byte []data = request.getData();
        int tag = NumberUtils.byteArrayToInt(data);

        Consumer<byte []> consumer = tag2ConsumerMap.get(tag);
        if (null != consumer) {
            byte []realData = new byte[data.length - 4];
            System.arraycopy(data, 4, realData, 0, realData.length);
            if (realData.length != 4) {
                consumer.accept(realData);
            } else {
                int closeFlag = NumberUtils.byteArrayToInt(realData);
                if (closeFlag == ReverseConstants.CLOSE_MAGIC) {
                    Callback callback = tag2ClosedMap.get(tag);
                    tag2ConsumerMap.remove(consumer);
                    if (null != callback) {
                        callback.call();
                    }
                }
            }
        }
    }

    @Override
    public void onClose(ChannelHandlerContext ctx) {
        write(() -> channels.remove(ctx.channel()));
        log.info("onClose remove");
        for (Callback callback : tag2ClosedMap.values()) {
            callback.call();
        }

        tag2ClosedMap.clear();
        tag2ConsumerMap.clear();
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
