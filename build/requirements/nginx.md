# Download

**官网** https://nginx.org/  
**下载** https://nginx.org/en/download.html

# 安装

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

# 系统服务

/etc/systemd/system/nginx.service

```shell
[Unit]
Description=The Nginx HTTP and reverse proxy server
After=network.target

[Service]
Type=forking
PIDFile=/usr/local/nginx/logs/nginx.pid
ExecStartPre=/usr/local/nginx/sbin/nginx -t
ExecStart=/usr/local/nginx/sbin/nginx
ExecReload=/usr/local/nginx/sbin/nginx -s reload
ExecStop=/usr/local/nginx/sbin/nginx -s stop
PrivateTmp=true
Restart=on-failure
RestartSec=5s

[Install]
WantedBy=multi-user.target
```

```shell
# 添加后需要开启
systemctl enable nginx
systemctl start|restart|stop|reload|status nginx
# 查看日志
sudo journalctl -u nginx.service
# 也可以
less /var/log/messages
```

# 配置

conf/nginx.conf

```
# 全局配置：Worker进程数=CPU核心数（生产最优）
worker_processes auto;

# 性能优化：开启Worker进程的文件描述符上限（与worker_connections匹配）
worker_rlimit_nofile 65535;

# 事件驱动配置（核心）
events {
    # 单Worker最大连接数，生产推荐2048/4096/8192（按需调大）
    worker_connections 4096;
    # 启用epoll模型（Linux下高性能IO多路复用，默认也是）
    use epoll;
    # 每个Worker进程同时接受多个新连接（优化连接建立速度）
    multi_accept on;
}
```

include vhosts/*.conf;

```
# conf/vhosts/admin.seezoon.com.conf

upstream admin-server {
    server 127.0.0.1:8080 max_fails=3 fail_timeout=10s;
    # 维持空闲长连接，不是最大链接限制
    keepalive 32;
}

server {
    listen       80;
    server_name  admin.seezoon.com;
    rewrite ^(.*)$  https://$host$1 permanent;
}

server {
    listen       443 ssl;
    server_name  admin.seezoon.com;
    # ssl on; 新版不支持了，listen后添加ssl
    ssl_certificate   /data/cert/admin.seezoon.com.pem; 
    ssl_certificate_key  /data/cert/admin.seezoon.com.key;
    ssl_session_timeout 5m;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
    ssl_prefer_server_ciphers on;

    # api
    location ^~ /api/ {
        # 默认1.0 无keepalive
        proxy_http_version 1.1;
        # 默认会加上connetion:close,无法长链接
        proxy_set_header Connection "";
        proxy_redirect off;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Real-PORT $remote_port;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        # 不以/ 结束，则路径会包含api
        proxy_pass http://admin-server/;
    }
    # 文件服务
    location ^~ /file/ {
        access_log off;
        alias /data/upload-server/;
    }

    # 静态资源
    location / {
        access_log off;
        root /data/admin-web/;
        index index.html index.htm;
    }

}
```

# 缺少openssl

```text
./configure: error: SSL modules require the OpenSSL library.
You can either do not enable the modules, or install the OpenSSL library
into the system, or build the OpenSSL library statically from the source
with nginx by using --with-openssl=<path> option.
```

**安装openssl**

```shell
cd /usr/local
wget https://github.com/openssl/openssl/releases/download/openssl-3.0.16/openssl-3.0.16.tar.gz
tar -zxvf openssl-3.0.16.tar.gz
cd openssl-3.0.16
# 可以查看INSTALL.md  说明
./Configure --prefix=/usr/local/openssl3 --openssldir=/usr/local/openssl3
make && make install
# 主要是这步骤 openssl3 目录调整，有的服务依赖的是lib目录
ln -s /usr/local/openssl3/lib64 /usr/local/openssl3/lib
```

# 常用知识

## root vs alias

root与alias主要区别在于nginx如何解释location后面的uri，这会使两者分别以不同的方式将请求映射到服务器文件上。

- root的处理结果是：包含location中的路径
- alias的处理结果是：不包含location中的路径
  还有一个重要的区别是alias后面必须要用“/”结束，否则会找不到文件的。而root则可有可无~~

```
# 访问/a/b/1，alias会查找 /data/seezoon/upload-server/a/b/1 
location ^~ /a/ {
        access_log off;
        alias /data/seezoon/upload-server/;
    }
# 访问/a/b/1，root会查找 /data/seezoon/upload-server/a/b/1 
location ^~ /a/ {
        access_log off;
        root /data/seezoon/upload-server/;
    }
```

## nginx location

```
location  = / {
  # 精确匹配 / ，主机名后面不能带任何字符串
  [ configuration A ] 
}

location  / {
  # 因为所有的地址都以 / 开头，所以这条规则将匹配到所有请求
  # 但是正则和最长字符串会优先匹配
  [ configuration B ] 
}

location /documents/ {
  # 匹配任何以 /documents/ 开头的地址，匹配符合以后，还要继续往下搜索
  # 只有后面的正则表达式没有匹配到时，这一条才会采用这一条
  [ configuration C ] 
}

location ~ /documents/Abc {
  # 匹配任何以 /documents/ 开头的地址，匹配符合以后，还要继续往下搜索
  # 只有后面的正则表达式没有匹配到时，这一条才会采用这一条
  [ configuration CC ] 
}

location ^~ /images/ {
  # 匹配任何以 /images/ 开头的地址，匹配符合以后，停止往下搜索正则，采用这一条。
  [ configuration D ] 
}

location ~* \.(gif|jpg|jpeg)$ {
  # 匹配所有以 gif,jpg或jpeg 结尾的请求
  # 然而，所有请求 /images/ 下的图片会被 config D 处理，因为 ^~ 到达不了这一条正则
  [ configuration E ] 
}

location /images/ {
  # 字符匹配到 /images/，继续往下，会发现 ^~ 存在
  [ configuration F ] 
}

location /images/abc {
  # 最长字符匹配到 /images/abc，继续往下，会发现 ^~ 存在
  # F与G的放置顺序是没有关系的
  [ configuration G ] 
}

location ~ /images/abc/ {
  # 只有去掉 config D 才有效：先最长匹配 config G 开头的地址，继续往下搜索，匹配到这一条正则，采用
    [ configuration H ] 
}

location ~* /js/.*/\.js

```

- =开头表示精确匹配 ，如 A 中只匹配根目录结尾的请求，后面不能带任何字符串。
- ^~ 开头表示uri以某个常规字符串开头，不是正则匹配
- ~ 开头表示区分大小写的正则匹配;
- ~* 开头表示不区分大小写的正则匹配
- / 通用匹配, 如果没有其它匹配,任何请求都会匹配到

顺序优先级：
(location =) > (location 完整路径) > (location ^~ 路径) > (location ~,~* 正则顺序) > (location
部分起始路径) > (/)

# rewrite

该指令是通过正则表达式的使用来改变URI。可以同时存在一个或多个指令。需要按照顺序依次对URL进行匹配和处理。

该指令可以在server块或location块中配置，其基本语法结构如下：

```
rewrite regex replacement [flag];
```

**regex**的含义：用于匹配URI的正则表达式。
replacement：将regex正则匹配到的内容替换成 replacement。
flag: flag标记。 flag有如下值：

- last: 本条规则匹配完成后，继续向下匹配新的location URI 规则。(不常用)
- break: 本条规则匹配完成即终止，不再匹配后面的任何规则(不常用)。
- redirect: 返回302临时重定向，浏览器地址会显示跳转新的URL地址。
- permanent: 返回301永久重定向。浏览器地址会显示跳转新的URL地址。

```
# 永久跳转到某个地址
rewrite ^/(.*) http://www.baidu.com/$1 permanent;
# 自动跳到某个路径，重新在匹配合适的location
location = / {
         rewrite (.*) /v2 last;
    }
```