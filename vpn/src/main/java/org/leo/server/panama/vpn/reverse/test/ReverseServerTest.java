package org.leo.server.panama.vpn.reverse.test;

import org.leo.server.panama.client.Client;
import org.leo.server.panama.client.ClientResponseDelegate;
import org.leo.server.panama.core.connector.impl.TCPResponse;
import org.leo.server.panama.util.NumberUtils;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.reverse.client.ReverseTCPClient;
import org.leo.server.panama.vpn.reverse.constant.ReverseConstants;
import org.leo.server.panama.vpn.reverse.core.ReverseCoreClient;
import org.leo.server.panama.vpn.reverse.core.ReverseCoreServer;
import org.leo.server.panama.vpn.reverse.protocol.ReverseProtocol;
import org.leo.server.panama.vpn.reverse.server.ReverseShadowSocksServer;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author xuyangze
 * @date 2019/7/16 4:16 PM
 */
public class ReverseServerTest {
    public static byte[] byteMerger(int ...values){
        byte[] results = new byte[values.length * 4];
        for (int index = 0; index < values.length; index++) {
            int value = values[index];
            byte[] bytes = NumberUtils.intToByteArray(value);
            System.arraycopy(bytes, 0, results, index * 4, bytes.length);
        }

        return results;
    }

    public static byte[] byteMerger(byte[] ...values){
        int total = 0;
        for (byte []value : values) {
            total += value.length;
        }

        byte[] results = new byte[total];
        int current = 0;
        for (int index = 0; index < values.length; index++) {
            byte []value = values[index];
            System.arraycopy(value, 0, results, current, value.length);
            current += value.length;
        }

        return results;
    }

    public static ReverseCoreServer start() {
        ReverseCoreServer reverseCoreServer = new ReverseCoreServer(8081);

        new Thread(() -> {
            reverseCoreServer.start();
        }).start();

        return reverseCoreServer;
    }

    public static void main2(String []args) {
        // 外网服务器
        ReverseCoreServer reverseCoreServer = ReverseServerTest.start();

        // 内网服务器
        ReverseCoreClient reverseCoreClient = new ReverseCoreClient(new InetSocketAddress("127.0.0.1", 8081), (data) -> {
            List<ReverseProtocol.ReverseProtocolData> reverseProtocolDatas = ReverseProtocol.decodeProtocol(data);

            if (null == reverseProtocolDatas || reverseProtocolDatas.size() == 0) {
                System.out.println("reverseProtocolDatas is empty, but data size is: " + data.length);
                return;
            }

            reverseProtocolDatas.forEach(reverseProtocolData -> {
                System.out.println("reverseProtocolData tag: " + reverseProtocolData.getTag() + " values: " + new String(reverseProtocolData.getData()));
            });
        });

        reverseCoreClient.connect(null);

        try {
            Thread.sleep(3000);
        } catch (Exception e) {

        }

        for (int tag = 0; tag < 10; tag ++) {
            // 外网服务向内网服务发送请求
            reverseCoreServer.send2Client(tag, "hello".getBytes(), (data) -> {
                System.out.println("receive:" + new String(data));
            }, () ->{
                System.out.println("closed");
            });
        }

        for (int tag = 0; tag < 10; tag ++) {
            // 内网服务向外网服务发送返回数据
            String message = "Hello Leo";
            byte []data = byteMerger(NumberUtils.intToByteArray(tag), NumberUtils.intToByteArray(message.getBytes().length), message.getBytes());
            reverseCoreClient.send(data, 2000);
        }

        // 关闭连接
        for (int tag = 0; tag < 10; tag++) {
            byte []close = byteMerger(tag, 4, ReverseConstants.CLOSE_MAGIC);
            reverseCoreClient.send(close, 2000);
        }
    }

    public static void main(String []args) {
        // 外网服务器
        ReverseCoreServer reverseCoreServer = ReverseServerTest.start();
        ReverseTCPClient reverseTCPClient = new ReverseTCPClient(new ClientResponseDelegate<TCPResponse>() {
            @Override
            public boolean shouldDoPerResponse() {
                return false;
            }

            @Override
            public boolean shouldDoCompleteResponse() {
                return true;
            }

            @Override
            public void onResponseComplete(Client client) {

            }

            @Override
            public void doCompleteResponse(Client client, TCPResponse response) {
                System.out.println("receive:" + response.getMessage());
            }

            @Override
            public void doPerResponse(Client client, TCPResponse response) {
                System.out.println("receive:" + response.getMessage());
            }

            @Override
            public void onConnectClosed(Client client) {
                System.out.println("close");
            }
        }, reverseCoreServer);

        reverseTCPClient.connect(null);

        ShadowSocksConfiguration shadowSocksConfiguration = new ShadowSocksConfiguration();
        shadowSocksConfiguration.setReversePort(8081);
        shadowSocksConfiguration.setReverseHost("127.0.0.1");
        shadowSocksConfiguration.setEncrypt("raw");

        ReverseShadowSocksServer reverseShadowSocksServer = new ReverseShadowSocksServer(shadowSocksConfiguration);

        reverseShadowSocksServer.start();

        try {
            Thread.sleep(3000);
        } catch (Exception e) {

        }

        for (int tag = 0; tag < 10; tag ++) {
            // 外网服务向内网服务发送请求
            String message = "hello_" + tag;
            reverseTCPClient.send(message.getBytes(), 2000);
        }

//        // 关闭连接
//        for (int tag = 0; tag < 10; tag++) {
//            reverseTCPClient.send(NumberUtils.intToByteArray(ReverseConstants.CLOSE_MAGIC), 2000);
//        }
    }
}
