package org.leo.server.panama.vpn.shadowsocks;

import java.util.Arrays;

public class ShadowsocksRequestResolver extends AbstractRequestResolver {

    public ShadowsocksRequestResolver() {
    }

    /**
     * <pre>
     * TCP Request:
     * +-------------------+----------------------------+
     * |  SOCKS5 FIELD (3) |      SHADOWSOCKS REQ       |
     * +-----+-----+-------+------+----------+----------+
     * | VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
     * +-----+-----+-------+------+----------+----------+
     * |  1  |  1  | X'00' |  1   | Variable |    2     |
     * +-----+-----+-------+------+----------+----------+
     *
     * UDP Request:
     *       +-------------------------------------------------------------+
     *       |                        AGENTX HEADER                        |
     * +-----+---------------------+---------------------------------------+
     * |      SOCKS5 FIELD (3)     |            SHADOWSOCKS REQ            |
     * +-----+---------------------+------+----------+----------+----------+
     * | RSV |  FRAG (FAKE-ATYP)   | ATYP | DST.ADDR | DST.PORT |   DATA   |
     * +----+----------------------+------+----------+----------+----------+
     * |  2  |        X'00'        |  1   | Variable |    2     | Variable |
     * +-----+---------------------+------+----------+----------+----------+
     * </pre>
     * Notice: In AgentX implementation, we send udp traffic though a secure tcp tunnel,
     * to distinct these two types of tcp payload, we add an extra 0 byte in front of any
     * udp-target requests. Thus, if we find a ATYP equals to 0, after truncate the first
     * byte, we can get the accurate udp-target request.
     */
    @Override
    public byte[] wrap(ShadowSocksRequest.Channel channel, final byte[] bytes) {
        if (channel == ShadowSocksRequest.Channel.UDP)
            return Arrays.copyOfRange(bytes, 2, bytes.length);
        else
            return Arrays.copyOfRange(bytes, 3, bytes.length);
    }

    @Override
    public ShadowSocksRequest parse(byte[] bytes) {
        boolean udp = false;
        ShadowSocksRequest.Type atyp;
        String host;
        int port, subsequentDataLength;

        // mark udp payload
        if (bytes[0] == 0) {
            udp = true;
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }

        switch (bytes[0]) {
            case Socks5.ATYP_IPV4:
                atyp = ShadowSocksRequest.Type.IPV4;
                host = "" + (bytes[1] & 0xff) + "." + (bytes[2] & 0xff)
                        + "." + (bytes[3] & 0xff) + "." + (bytes[4] & 0xff);
                port = ((bytes[5] & 0xff) << 8) | (bytes[6] & 0xff);
                subsequentDataLength = bytes.length - 7;
                break;
            case Socks5.ATYP_DOMAIN:
                atyp = ShadowSocksRequest.Type.DOMAIN;
                int length = bytes[1] & 0xff;
                host = new String(bytes, 2, length);
                port = ((bytes[length + 2] & 0xff) << 8) + (bytes[length + 3] & 0xff);
                subsequentDataLength = bytes.length - 4 - length;
                break;
            case Socks5.ATYP_IPV6:
                atyp = ShadowSocksRequest.Type.IPV6;
                host = String.format(
                        "%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x", bytes[1] & 0xff
                        , bytes[2] & 0xff, bytes[3] & 0xff, bytes[4] & 0xff, bytes[5] & 0xff, bytes[6] & 0xff
                        , bytes[7] & 0xff, bytes[8] & 0xff, bytes[9] & 0xff, bytes[10] & 0xff, bytes[11] & 0xff
                        , bytes[12] & 0xff, bytes[13] & 0xff, bytes[14] & 0xff, bytes[15] & 0xff, bytes[16] & 0xff
                );
                port = ((bytes[17] & 0xff) << 8) + (bytes[18] & 0xff);
                subsequentDataLength = bytes.length - 19;
                break;
            default:
                return new ShadowSocksRequest(ShadowSocksRequest.Type.UNKNOWN, null, -1, 0);
        }
        return new ShadowSocksRequest(atyp, host, port, subsequentDataLength).setChannel(udp ? ShadowSocksRequest.Channel.UDP : ShadowSocksRequest.Channel.TCP);
    }

    @Override
    public boolean exposeRequest() {
        return false;
    }
}
