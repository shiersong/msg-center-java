#Java版的消息中心：

#环境要求\
1、JDK1.8

#目前支持的消息服务\
1、MQTT协议（需要第三方MQTT服务器支持）\
2、Web Socket协议.

#本消息中心支持消息生产者还是消费者\
本消息中心是消息生产者，访问具体的MQTT broker，并且对外提供HTTP接口供其他业务程序调用，\
也就是说其他业务程序调用本消息中心的HTTP接口，将消息推送给MQTT broker或者通过Web Socket\
协议将消息推送给具体的客户端。

#配置\
具体配置请参考src/main/resources/application.yml

#使用方法\
1、消息发送方\
消息发送方其使用HTTP协议，以同步的方式发送消息到消息中心。\
具体请求和响应报文格式如下：\
请求地址：\
http://{消息中心IP}:{消息中心端口}/msgPublisher/sendMsg  \
请求方法：\
POST  \
请求头：\
content-type为application/json;charset=utf-8   \
请求报文样例：\
{
"clientIds": [
"0348"
],
"message": {"test": "这是一个测试"},
"msgType": "MQTT",
"retained": false,
"topic": "test"
}  \
请求报文各字段描述：\
msgType：消息类型，文本类型，不可空。MQTT表示MQTT协议的消息, WEBSOCKET表示Web Socket协议的消息。业务消息请求报文中指定了具体的msgType，消息中心就会根据这个指定的使用相应的协议和客户端进行通信。\
message：消息内容，任何类型数据均可，不可空。实际的业务消息，消息中心会透传这个消息，不会对这个消息进行加工。\
topic：消息主题,文本类型，如果msgType参数为MQTT，则此参数必须传入；如果msgType参数不为MQTT，则此参数不需要传入。表示通过MQTT协议向这个主题上发送了消息，消息接收方订阅这个主题的消息即可收到具体的消息。\
clientIds：需要发送给具体Web Socket消息接收方的客户端ID，文本数组，可空。实际业务上一般是用户ID，只有在msgType参数为WEBSOCKET的时候，此字段才有效；其它的时候忽略此字段。如果此参数为空，则向所有连接上的Web Socket消息接收方发送消息。\
retained：是否通知MQTT服务器保留消息（true-保留；空或者false-不保留），布尔类型，可空。\
响应报文样例：\
{
"msg": "操作成功",
"code": 200
}   \
响应报文各字段描述：\
code：表示成功/失败编码，整数类型，不可空。200代表操作成功；其余代表操作失败。\
msg：表示成功/失败信息，文本类型，可空。\

2、消息接收方：\

2.1、MQTT消息接收方：\
订阅地址：\
MQTT服务器IP：第三方MQTT服务器IP；\
MQTT服务器端口：第三方MQTT服务器端口。\
消息格式：\
消息是json格式，它的样例如下：\
{
"id": "05595d80-4a98-4658-b6d5-04a78f30e58f",
"data": 具体的业务消息
}   \
注意事项：\
现有的MQTT服务器有的时候会发送两条同样的消息给客户端，客户端需要根据“消息唯一标识”去重。\


2.2、Web Socket消息接收方：\
连接地址：\
ws://{消息中心IP}:{消息中心端口}/ws/ {clientId}   \
其中{clientId}表示当前消息接收方客户端ID，一般情况下为用户ID，具体情况需要消息接收方和消息发送方商定。\
消息格式：\
消息是json格式，它的样例如下：\
{
"id": "05595d80-4a98-4658-b6d5-04a78f30e58f",
"data": 具体的业务消息
}   \
注意事项：\
消息中心不接受一般Web Socket SDK自带的ping/pong形式的心跳。消息接收方需要每隔60秒（或者60秒以内）发送一次心跳到消息中心，心跳的内容不限。
消息中心回应消息发送方的心跳数据格式是：{"data":{"code": 0, "msg": "OK", "heartBeatTime": "系统时间(yyyy-MM-dd HH:mm:ss.SSS形式)"},"id":"9e29c392-409e-4a47-88c8-69d413ef2d66"}
