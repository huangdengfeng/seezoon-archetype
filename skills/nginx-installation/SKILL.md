---
name: nginx-installation
description: "用于用户需要在linux安装及配置nginx的场景"
license: Apache-2.0
metadata:
  author: huangdengfeng
  version: "1.0.0"
---

# nginx安装及配置

## 安装

可以根据官网的版本情况，下载最新稳定版本。[下载页面](https://nginx.org/en/download.html)

```shell
cd /usr/local
wget https://nginx.org/download/nginx-1.27.4.tar.gz
tar -zxvf nginx-1.27.4.tar.gz
cd nginx-1.27.4
# 可以查看 可选，一般主要配置目录即可
./configure --help
# 开启ssl 支持http2,如果不指定openssl 路径不报错则不管,--with-openssl 是源码目录
./configure --prefix=/usr/local/nginx --with-http_ssl_module --with-http_v2_module [--with-openssl=/usr/local/openssl-3.0.16]
# -j核数
make -j8 && make install

cd nginx/sbin
#启动
./nginx 
# 测试
./nginx -t
# 热加载
./nginx -s reload 
# 如果报错则pkill nginx
./nginx -s stop 
```