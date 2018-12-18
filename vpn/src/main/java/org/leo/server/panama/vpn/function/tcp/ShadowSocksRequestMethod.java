//package org.leo.server.panama.vpn.function.tcp;
//
//import com.google.common.cache.Cache;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.nio.NioEventLoopGroup;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.leo.server.panama.client.Client;
//import org.leo.server.panama.client.ClientResponseDelegate;
//import org.leo.server.panama.client.tcp.TCPClient;
//import org.leo.server.panama.core.connector.impl.TCPRequest;
//import org.leo.server.panama.core.connector.impl.TCPResponse;
//import org.leo.server.panama.core.method.RequestMethod;
//import org.leo.server.panama.spring.function.annotation.RequestFunction;
//import org.leo.server.panama.spring.function.service.TCPRequestService;
//import org.leo.server.panama.vpn.ShadowSocksConfiguration;
//import org.leo.server.panama.vpn.constant.VPNConstant;
//import org.leo.server.panama.vpn.security.wrapper.Wrapper;
//import org.leo.server.panama.vpn.security.wrapper.WrapperFactory;
//import org.leo.server.panama.vpn.shadowsocks.ShadowSocksRequest;
//import org.leo.server.panama.vpn.shadowsocks.ShadowsocksRequestResolver;
//import org.leo.server.panama.vpn.util.Callback;
//import org.leo.server.panama.vpn.util.DNSResolver;
//import org.leo.server.panama.vpn.util.LocalCacheFactory;
//
//import java.util.Arrays;
//
//@RequestFunction(name = "shadowsocks", method = RequestMethod.TCP)
//public class ShadowSocksRequestMethod implements TCPRequestService {
//    private final static Log log = LogFactory.getLog(ShadowSocksRequestMethod.class);
//
//    private Cache<Channel, Proxy> channel2ProxyCache = LocalCacheFactory.createCache(5 * 60 * 1000, 2000);
//    private NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(VPNConstant.MAX_CLIENT_THREAD_COUNT);
//    private ShadowsocksRequestResolver requestResolver = new ShadowsocksRequestResolver();
//
//    @Override
//    public TCPResponse execute(TCPRequest request) {
//        Channel channel = request.getChannelHandlerContext().channel();
//        Proxy proxy = channel2ProxyCache.getIfPresent(channel);
//        if (null == proxy) {
//            proxy = new Proxy(request.getChannelHandlerContext().channel(),
//                    () -> channel2ProxyCache.invalidate(channel),
//                    ShadowSocksConfiguration.getType(), ShadowSocksConfiguration.getPassword());
//
//            channel2ProxyCache.put(channel, proxy);
//        }
//
//        proxy.send2Target(request.getData());
//        return null;
//    }
//
//    class Proxy implements ClientResponseDelegate<TCPResponse> {
//        private Channel clientChannel;
//        private Wrapper wrapper;
//        private Client redirectClient;
//        private Callback finish;
//
//        public Proxy(Channel clientChannel, Callback finish, String encryption, String password) {
//            this.clientChannel = clientChannel;
//            wrapper = WrapperFactory.getInstance(encryption, password, "encrypt");
//            this.finish = finish;
//        }
//
//        @Override
//        public boolean shouldDoPerResponse() {
//            return true;
//        }
//
//        @Override
//        public boolean shouldDoCompleteResponse() {
//            return false;
//        }
//
//        @Override
//        public void doPerResponse(Client client, TCPResponse response) {
//            // target -> proxy -> client
//            log.info(" proxy <---------------- target " + response.getData().length + " byte");
//            send2Client(response.getData());
//        }
//
//        @Override
//        public void onConnectClosed(Client client) {
//            // send close data
//            log.info("client <----------------  proxy closed");
//            clientChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//            if (null != finish) {
//                try {
//                    finish.call();
//                } catch (Exception e) {
//                    //
//                }
//            }
//        }
//
//        public void send2Client(byte []data) {
//            data = wrapper.wrap(data);
//            clientChannel.write(Unpooled.wrappedBuffer(data));
//            clientChannel.flush();
//            log.info("client <----------------  proxy " + data.length + " byte");
//        }
//
//        public void send2Target(byte []data) {
//            log.info("client ---------------->  proxy " + data.length + " byte");
//            byte []decryptData = wrapper.unwrap(data);
//            if (null == redirectClient) {
//                ShadowSocksRequest shadowSocksRequest = requestResolver.parse(decryptData);
//
//                if (shadowSocksRequest.getAtyp() == ShadowSocksRequest.Type.UNKNOWN) {
//                    throw new RuntimeException("unknown request type: " + decryptData[0]);
//                }
//
//                log.info(" proxy ----------------> target " + shadowSocksRequest.getHost() + ":" + shadowSocksRequest.getPort());
//                redirectClient = new TCPClient(eventLoopGroup, Proxy.this);
//                redirectClient.connect(DNSResolver.resolver(shadowSocksRequest.getHost(), shadowSocksRequest.getPort()));
//
//                if (shadowSocksRequest.getChannel() == ShadowSocksRequest.Channel.TCP) {
//                    int dataLength = shadowSocksRequest.getSubsequentDataLength();
//                    if (dataLength > 0) {
//                        decryptData = Arrays.copyOfRange(decryptData, decryptData.length - dataLength, decryptData.length);
//                    }
//
//                } else if (shadowSocksRequest.getChannel() == ShadowSocksRequest.Channel.UDP) {
//                    throw new RuntimeException("unsupport request type: udp");
//                }
//            }
//
//            redirectClient.send(decryptData, 0);
//            log.info(" proxy ----------------> target " + decryptData.length + " byte");
//        }
//    }
//}
