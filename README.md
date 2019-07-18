基于netty实现的shadowsocks服务端
====

使用
---
1.java 8或者以上版本
2.下载panama.jar或者使用maven3以上进行打包，生成panama.jar<br>
3.在panama.jar同级目录下配置panama.config<br>
4.执行命令java -jar panama.jar<br>

panama.config:
```
{
	"password":"1234567890",
	"port":9898,
	"type":"aes-256-cfb"
}
```

配置详情
```
{
	"password":"1234567890", // 密钥
	"port":9898,			 // 启动端口
	"type":"aes-256-cfb"	 // 加密类型
}
```
---
功能清单

功能 | 描述
---- | ----
普通模式 | 使用shadowSock客户端连接服务器A，服务器A进行网络代理，然后返回用户请求数据
代理模式 | 使用shadowSock客户端连接代理服务器A，代理A向真实shadowSock服务器B发送请求，由B完成服务请求，然后返回响应数据给客户端
反向代理 | 存在一台外网代理服务器A，一台内网服务器B，shadowSock客户端连接A，客户端请求由A转发给B，前提是B能够访问到A


---
代理模式 

代理端panama.config:
```
{
	"mode":"proxy",
	"password":"1234567890",
	"type":"aes-256-cfb",
	"port":9898,
	"proxy": "127.0.0.1",
	"proxyPort":9899,
	"proxyType":"aes-256-cfb",
	"proxyPassword":"123456789"
}
```

配置详情
```
{
	"mode":"proxy",				// 启动模式，代理模式
	"password":"1234567890",	// 密钥
	"type":"aes-256-cfb",		// 加密类型
	"port":9898,				// 代理端启动端口
	"proxy": "127.0.0.1",		// 真实服务地址
	"proxyPort":9899,			// 服务端口
	"proxyType":"aes-256-cfb",	// 服务加密类型
	"proxyPassword":"123456789"	// 服务密钥
}
```

真实服务端端panama.config:
```
{
	"password":"123456789",
	"port":9899,
	"type":"aes-256-cfb"
}
```

配置详情
```
{
	"password":"123456789",		// 密钥，密钥需要同proxyType保持一致
	"port":9899,				// 启动端口，端口需要同proxyPort保持一致
	"type":"aes-256-cfb"		// 加密类型，加密类型同proxyType保持一致
}
```
---

---
反向代理模式，外部 panama.config:
```
{
	"mode":"outer",
	"password":"1234567890",
	"type":"aes-256-cfb",
	"port":9898,
	"proxyType":"aes-256-cfb",
	"proxyPassword":"123456789",
	"reversePort":9899
}
```

配置详情
```
{
	"mode":"outer",					// 模式，内网穿透，外网服务端
	"password":"1234567890",		// 外网连接密钥
	"type":"aes-256-cfb",			// 外网连接加密方式
	"port":9898,					// 外网启动端口
	"proxyType":"aes-256-cfb",		// 内网连接加密方式
	"proxyPassword":"123456789",	// 内网连接密钥
	"reversePort":9899				// 本机对内网服务暴露的反向代理端口
}
```

反向代理模式，内部 panama.config:
```
{
	"mode":"inner",
	"password":"123456789",
	"type":"aes-256-cfb",
	"reverseHost":"127.0.0.1",
	"reversePort":9899
}
```

配置详情
```
{
	"mode":"inner",					// 模式，内网穿透，内网服务端
	"password":"123456789",			// 内网服务器密钥
	"type":"aes-256-cfb",			// 内网服务器加密方式
	"reverseHost":"127.0.0.1",		// 外网服务器地址
	"reversePort":9899				// 外网服务器反向代理端口
}
```
---

