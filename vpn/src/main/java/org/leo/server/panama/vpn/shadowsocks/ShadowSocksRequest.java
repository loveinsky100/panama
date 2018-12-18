package org.leo.server.panama.vpn.shadowsocks;

public class ShadowSocksRequest {
    private Type atyp;
    private String host;
    private int port;
    private int subsequentDataLength;
    private Channel channel = Channel.TCP;

    public ShadowSocksRequest(Type atyp, String host, int port, int subsequentDataLength) {
        this.atyp = atyp;
        this.host = host;
        this.port = port;
        this.subsequentDataLength = subsequentDataLength;
    }

    public ShadowSocksRequest(byte[] bytes) {
        String[] target = new String(bytes).split(":");
        if (target[0].equals(Type.IPV4.name()))
            this.atyp = Type.IPV4;
        if (target[0].equals(Type.DOMAIN.name()))
            this.atyp = Type.DOMAIN;
        if (target[0].equals(Type.IPV6.name()))
            this.atyp = Type.IPV6;
        this.host = target[1];
        this.port = Integer.parseInt(target[2]);
        this.subsequentDataLength = Integer.parseInt(target[3]);
    }

    public Channel getChannel() {
        return channel;
    }

    public ShadowSocksRequest setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    public byte[] getBytes() {
        return (atyp.name() + ":" + host + ":" + port + ":" + subsequentDataLength).getBytes();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getSubsequentDataLength() {
        return subsequentDataLength;
    }

    public Type getAtyp() {
        return atyp;
    }

    @Override
    public String toString() {
        return "ShadowSocksRequest{" +
                "atyp=" + atyp +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", subsequentDataLength=" + subsequentDataLength +
                ", channel=" + channel +
                '}';
    }

    public enum Channel {
        TCP, UDP
    }

    public enum Type {
        IPV4, DOMAIN, IPV6, UNKNOWN
    }
}
