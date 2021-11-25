FROM registry.cn-hangzhou.aliyuncs.com/peachyy/java8base
LABEL maintainer="peachyy"
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo ${TZ} > /etc/timezone
RUN mkdir -p /opt
ADD target/peachyy-sdmq.tar peachyy-sdmq.tar
RUN tar -zxf  peachyy-sdmq.tar && cd peachyy-sdmq
WORKDIR /opt/peachyy-sdmq

ENTRYPOINT ["bin/sdmq","start"]
EXPOSE 6355