package org.leo.server.panama.vpn.shadowsocks;

public interface Socks5 {
    int VERSION = 5;
    int ATYP_IPV4 = 1;
    int ATYP_DOMAIN = 3;
    int ATYP_IPV6 = 4;
    // preset replies
    byte[] echo = {5, 0};
    byte[] reject = {5, (byte) 0xff};
    byte[] succeed = {5, 0, 0, 1, 0, 0, 0, 0, 0, 0};
    byte[] error_1 = {5, 1, 0, 1, 0, 0, 0, 0, 0, 0}; // general socks server failure
    byte[] error_2 = {5, 2, 0, 1, 0, 0, 0, 0, 0, 0}; // connection not allowed by rule set
    byte[] error_3 = {5, 3, 0, 1, 0, 0, 0, 0, 0, 0}; // network unreachable
    byte[] error_4 = {5, 4, 0, 1, 0, 0, 0, 0, 0, 0}; // host unreachable
    byte[] error_5 = {5, 5, 0, 1, 0, 0, 0, 0, 0, 0}; // connection refused
    byte[] error_6 = {5, 6, 0, 1, 0, 0, 0, 0, 0, 0}; // ttl expired
    byte[] error_7 = {5, 7, 0, 1, 0, 0, 0, 0, 0, 0}; // command not supported
    byte[] error_8 = {5, 8, 0, 1, 0, 0, 0, 0, 0, 0}; // address type not supported
}
