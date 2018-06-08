FROM registry.cn-hangzhou.aliyuncs.com/peachyy/java8base
VOLUME /tmp
ADD sdmq-core-0.0.1.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 6355
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]