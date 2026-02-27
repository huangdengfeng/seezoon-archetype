
# 基础镜像
FROM oraclelinux:10
# 维护者信息
LABEL maintainer="734839030@qq.com"
# usage
# docker buildx build  -t openjdk:21.0.2 .
# docker tag [imageId] sgccr.ccs.tencentyun.com/seezoon/openjdk:[tag]
# docker push sgccr.ccs.tencentyun.com/seezoon/openjdk:[tag]
# 定义构建参数（可选，方便后续修改 JDK 版本）
ARG JDK_VERSION=21.0.2
ARG JAVA_HOME=/usr/local/jdk-${JDK_VERSION}
ARG JDK_TAR_FILE=openjdk-${JDK_VERSION}_linux-x64_bin.tar.gz
ARG JDK_DOWNLOAD_URL=https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz

# 核心步骤：安装依赖 + 下载解压 JDK + 配置环境变量 + 清理缓存
# 指定解压目录需要去除一层目录
RUN set -eux; \
    dnf install -y wget tar gzip && dnf clean all; \
    wget --no-check-certificate ${JDK_DOWNLOAD_URL} -O ${JDK_TAR_FILE}; \
    mkdir -p ${JAVA_HOME}; \
    tar -zxf ${JDK_TAR_FILE} -C ${JAVA_HOME} --strip-components=1; \
    ${JAVA_HOME}/bin/java -version; \
    rm -rf ${JDK_TAR_FILE}

# 配置 JAVA 环境变量（容器内全局可用 java/javac 命令）
ENV JAVA_HOME=${JAVA_HOME}
ENV PATH=${JAVA_HOME}/bin:${PATH}

# 可选：容器启动时默认执行 java -version，验证环境是否正常
CMD ["java", "-version"]