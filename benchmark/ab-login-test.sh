#!/bin/bash

# ============================================================
# ab 压测脚本 - 账号密码登录接口
# 接口: POST /login/user_pwd
# ============================================================

# 配置参数
HOST=${HOST:-"localhost"}
PORT=${PORT:-"8080"}
USERNAME=${USERNAME:-"admin"}
PASSWORD=${PASSWORD:-"123456"}

# 压测参数
CONCURRENCY=${CONCURRENCY:-100}      # 并发数
REQUESTS=${REQUESTS:-10000}          # 总请求数

# 构建 URL
URL="http://${HOST}:${PORT}/login/user_pwd"

# 请求体 (JSON)
POST_DATA="{\"username\":\"${USERNAME}\",\"password\":\"${PASSWORD}\"}"

# 临时文件存放请求体
POST_FILE=$(mktemp)
echo "${POST_DATA}" > "${POST_FILE}"

echo "============================================================"
echo "ab 压测 - 账号密码登录接口"
echo "============================================================"
echo "URL:         ${URL}"
echo "并发数:      ${CONCURRENCY}"
echo "总请求数:    ${REQUESTS}"
echo "请求体:      ${POST_DATA}"
echo "============================================================"

# 执行 ab 压测
ab -n ${REQUESTS} \
   -c ${CONCURRENCY} \
   -p "${POST_FILE}" \
   -T "application/json" \
   -H "Accept: application/json" \
   "${URL}"

# 清理临时文件
rm -f "${POST_FILE}"

echo "============================================================"
echo "压测完成"
echo "============================================================"
