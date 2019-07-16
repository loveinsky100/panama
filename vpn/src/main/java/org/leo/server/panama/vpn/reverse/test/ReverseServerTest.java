package org.leo.server.panama.vpn.reverse.test;

import org.leo.server.panama.util.NumberUtils;
import org.leo.server.panama.vpn.configuration.ShadowSocksConfiguration;
import org.leo.server.panama.vpn.proxy.factory.ShadowSocksProxyFactory;
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
    public static ReverseCoreServer start() {
        ReverseCoreServer reverseCoreServer = new ReverseCoreServer(8081);

        new Thread(() -> {
            reverseCoreServer.start();
        }).start();

        return reverseCoreServer;
    }

    public static void main(String []args) {
        ReverseCoreServer reverseCoreServer = ReverseServerTest.start();
        ReverseCoreClient reverseCoreClient = new ReverseCoreClient(new InetSocketAddress("127.0.0.1", 8081), (data) -> {
            int tag = NumberUtils.byteArrayToInt(data);
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
            reverseCoreServer.send2Client(tag, ("hello" + tag).getBytes(), (data) -> {
                System.out.println("receive");
            }, () ->{
                System.out.println("closed");
            });
        }
    }
}
