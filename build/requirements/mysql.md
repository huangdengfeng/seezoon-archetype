# Download

**官网** https://www.mysql.com/

**下载地址** https://dev.mysql.com/downloads/mysql/

**选择版本**   
MySQL Community Server 8.4.4 LTS  
Linux - Generic  
Linux - Generic (glibc 2.28) (x86, 64-bit)

# 安装

```shell
cd /usr/local
wget https://cdn.mysql.com//Downloads/MySQL-8.4/mysql-8.4.4-linux-glibc2.28-x86_64.tar.xz
tar -xvf mysql-8.4.4-linux-glibc2.28-x86_64.tar.xz

# https://dev.mysql.com/doc/refman/8.4/en/binary-installation.html
# 安装步骤
groupadd mysql
useradd -r -g mysql -s /bin/false mysql
cd /usr/local
tar -xvf mysql-8.4.4-linux-glibc2.28-x86_64.tar.xz
ln -s mysql-8.4.4-linux-glibc2.28-x86_64 mysql
cd mysql
mkdir mysql-files
chown mysql:mysql mysql-files
chmod 750 mysql-files
# 会生成临时密码
bin/mysqld --initialize --user=mysql
# 启动命令
bin/mysqld_safe --user=mysql &
# Next command is optional 加入到系统服务 这个不适合新系统，可以参考里面start stop
cp support-files/mysql.server /etc/init.d/mysql.server
```

# 配置

A temporary password is generated for root@localhost: ho0Eqssri.!k

```shell
# 输入以下命令后，粘贴临时密码
mysql -u root -p
# 修改本地 root 密码
ALTER USER 'root'@'localhost' IDENTIFIED BY '新密码';

# 修改允许远程登录的 root 密码（可选）
ALTER USER 'root'@'%' IDENTIFIED BY '新密码';

# 仅允许从本地访问（安全推荐）
CREATE USER 'backup_user'@'localhost' IDENTIFIED BY 'StrongPassword123!';

# 允许从特定 IP 访问（如备份服务器）
CREATE USER 'backup_user'@'192.168.1.100' IDENTIFIED BY 'StrongPassword123!';

# 允许从任意 IP 访问（存在风险，谨慎使用）
CREATE USER 'backup_user'@'%' IDENTIFIED BY 'StrongPassword123!';
# 授予所有权限
GRANT ALL PRIVILEGES ON *.* TO 'username'@'host' WITH GRANT OPTION;
# 授予所有数据库的只读权限 + 锁表权限（适用于 mysqldump）
GRANT SELECT, RELOAD, LOCK TABLES, PROCESS, REPLICATION CLIENT ON *.* TO 'backup_user'@'localhost';

# 或授予指定数据库的备份权限（更安全）
GRANT SELECT, LOCK TABLES ON `db1`.* TO 'backup_user'@'localhost';
GRANT SELECT, LOCK TABLES ON `db2`.* TO 'backup_user'@'localhost';
FLUSH PRIVILEGES;
```

# 备份

https://dev.mysql.com/doc/refman/8.4/en/mysqldump.html

```shell
# 全部数据库（包含库名）
/usr/local/mysql/bin/mysqldump  -u用户名 -p密码 --all-databases > /保存路径/文件名.sql
# 指定数据库，不加--databases || -B  则不带库名
/usr/local/mysql/bin/mysqldump -u用户名 -p密码 --databases 数据库1 数据库2... > 保存路径/文件名.sql
# 导出单库不带库名
/usr/local/mysql/bin/mysqldump -u用户名 -p密码  数据库1 ... > 保存路径/文件名.sql

# 恢复 导出时候如果带库名则不需要指定DB，也可以指定库名，恢复指定库
mysql -uusername -ppassword [db1] < tb1tb2.sql

```

```shell
crontab -e
* 8 * * * /data/db_backup/cron_backup.sh >/dev/null  2>&1 &

# cron_backup.sh
#!/bin/bash
cd `dirname $0`
pwd
mkdir sqls 2>/dev/null
echo "clear sql files before one week ago."
find ./sqls/ -name "*.sql"  -ctime +30  -exec rm -rf 2>/dev/null {}  \;
echo "dump today's db ."

# --skip-extended-insert fobid multivalue insert sql
# --complete-insert use total sql include col name

mysqldump --single-transaction -u${user} -p${password} -h ${host} -P ${port} --default-character-set=utf8mb4 --skip-extended-insert --complete-insert --ignore-table=${ignoreTable} -B  ${database} > sqls/${database}.`date +%Y%m%d`.sql

```

# 制作系统服务

`/etc/systemd/system/mysql.service`

```shell
[Unit]
Description=MySQL Server
After=network.target

[Service]
ExecStart=/usr/local/mysql/bin/mysqld_safe --user=mysql
User=mysql
Group=mysql
# 重启策略：仅在非正常退出时重启

Restart=on-failure

[Install]
WantedBy=multi-user.target
```

```shell
# 一定要启用
systemctl enable mysql
# 查看是否开启
systemctl is-enabled mysql
# 修改系统服务文件后一定要
systemctl daemon-reload
sudo systemctl start|stop|status mysql
```

# 通过binlog 看sql

```shell
/usr/local/zftmysql/bin/mysqlbinlog --base64-output=decode-rows -v --start-datetime='2022-04-25 20:00:00' --stop-datetime='2022-04-25 21:30:00' binlog/binlog.000038

```

# 常用sql

```shell
# 查看进程
SHOW FULL PROCESSLIST;
# 查看事务
select * from INFORMATION_SCHEMA.INNODB_TRX;
# 查看事务锁或者去掉条件查看事务情况
SELECT t.trx_started,t.trx_mysql_thread_id,t.trx_rows_locked,t.trx_query,t.trx_lock_structs FROM INFORMATION_SCHEMA.INNODB_TRX t 
where t.trx_state like '%LOCK%' order by t.trx_started asc;
kill t.trx_mysql_thread_id;
```

