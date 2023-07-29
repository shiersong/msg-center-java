FROM openjdk:8-jre

MAINTAINER Scott Soong

EXPOSE 5001

###应用的HTTP、Web Socket端口
ENV JAVA_OPTS ""
###应用的HTTP、Web Socket端口
ENV PORT 5001
###MQTT服务器IP
ENV MQTT_SERVER_IP 127.0.0.1
###MQTT服务器端口
ENV MQTT_SERVER_PORT 32567
###MQTT服务器用户名
ENV MQTT_SERVER_USERNAME guest
###MQTT服务器密码
ENV MQTT_SERVER_PASSWORD dfdfsafe
###M连接MQTT服务器的客户端ID
ENV MQTT_CLIENT_ID ""
###访问MQTT服务器的连接超时时间，单位为秒
ENV MQTT_CONNECTION_TIMEOUT 30
###和MQTT服务器之间发送心跳的时间间隔，单位为秒
ENV MQTT_KEEP_ALIVE_INTERVAL 30
###发送MQTT消息到MQTT服务器的默认QOS
ENV MQTT_DEFAULT_QOS 1
###发送MQTT消息到MQTT服务器的默认RETAINED
ENV MQTT_DEFAULT_RETAINED false
###日志级别
ENV LOG_LEVEL debug
###Web Socket客户端心跳间隔，超过这个间隔（单位：毫秒），就会断开和客户端的连接
ENV WEB_SOCKET_MAX_IDLE 60000
###Web Socket所使用的MQTT topic，接到Web Socket消息后会先把消息发送到MQTT服务器上这个topic，
###用于解决Web Socket在集群情况没有把消息发送到对应的client id的客户端的问题，也就是说业务消息中的topic不能和这个topic重名
###消息中心消费这个消息，判断当前服务器是否连接了消息中指定的client id的客户端，如果连接了，则把消息推送到具体的客户端；如果没有连接，则忽略这个消息
ENV WEB_SOCKET_MQTT_INNER_TOPIC inner/websocket
###Web Socket所使用的MQTT topic的默认QOS
ENV WEB_SOCKET_MQTT_DEFAULT_QOS 2
###Web Socket所使用的MQTT topic中的消息处理时长，单位毫秒
ENV MQTT_COMPLETION_TIMEOUT 30000

# 创建目录
RUN mkdir -p /home/msg-center
# 指定路径
WORKDIR /home/msg-center
# 复制jar文件到路径
COPY msg-center.jar /home/msg-center/msg-center.jar
# 复制启动脚本到路径
COPY start-server.sh /home/msg-center/start-server.sh
# 启动网关服务
ENTRYPOINT ["sh","start-server.sh"]
