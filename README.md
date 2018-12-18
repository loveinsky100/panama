基于netty实现的shadowsocks服务端
====

使用
---
1.ShadowSocks.java修改默认的加密类型(aes-256-cfb)和密码(1234567890)<br>
2.maven打包，生成panama.jar包<br>
3.执行命令java -jar panama.jar<br>

```
ShadowSocksConfiguration.setType("aes-256-cfb");
ShadowSocksConfiguration.setPassword("1234567890");

Server server = new TCPServer(9898, new AgentShadowSocksRequestHandler());
server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
```

---
更多功能

1.服务端代理
客户端通过aes-256-cfb | 1234567890连接此服务端，此服务端将请求转发到真实的ss服务器
```
ShadowSocksConfiguration.setType("aes-256-cfb");
ShadowSocksConfiguration.setPassword("1234567890");

// 配置代理信息
ShadowSocksConfiguration.setProxy("your proxy ip");
ShadowSocksConfiguration.setProxyType("your proxy type like aes-256-cfb");
ShadowSocksConfiguration.setProxyPwd("you proxy password");
ShadowSocksConfiguration.setProxyPort(9898); // your proxy port

Server server = new TCPServer(9898, new AgentShadowSocksRequestHandler());
server.start(VPNConstant.MAX_SERVER_THREAD_COUNT);
```

2.反向代理(开发中)
有一台服务器在外部网络中，一台服务器在内网（比如公司内网），开启后用户通过ss客户端连接到外部网络服务器，通过反向代理方式使内网机器调用真实请求，然后返回数据给外网服务，外网服务再返回数据给客户端


