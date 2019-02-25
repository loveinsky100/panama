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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 反向代理服务端，实际上是作为客户端存在内网服务器中
 * 想代理服务器发起一条长连接，接收代码服务器的请求
 * 收到代理服务器请求后发起网络请求，然后回调给代理服务器
 * 由于复用一条连接，所有的请求带上tag标记，判断是否属于同一个请求
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

        // 生成tag
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
