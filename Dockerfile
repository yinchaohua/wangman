FROM java:8
MAINTAINER "wanyadi"<327432374@qq.com>
VOLUME /tmp
ENV TZ=Asia/Shanghai
ADD /target/star-node.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
