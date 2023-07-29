package org.msg.msgcenter.httprequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

@ApiModel()
@Valid
public class MsgControllerSendMsgReq {

    @ApiModelProperty(
            value = "消息类型（MQTT-MQTT协议的消息, WEBSOCKET-Web Socket协议的消息, SMS-短短信, LMS-长短信）",
            required = true,
            allowableValues = "MQTT, WEBSOCKET, SMS, LMS"
    )
    @NotEmpty(message = "请指定消息类型")
    private String msgType;

    @ApiModelProperty(value = "消息", required = true)
    @NotNull(message = "请不要发送空消息")
    private Object message;

    @ApiModelProperty("主题（如果msgType参数为MQTT，则此参数必须传入；如果msgType参数不为MQTT，则此参数不需要传入）")
    private String topic;

    @ApiModelProperty("是否通知MQTT服务器保留消息（true-保留；空或者false-不保留），如果保留消息，消息订阅者每次开始订阅的时候都会收到这个消息，而不论上一次订阅是否收到过这个消息")
    private Boolean retained;
/*
    @ApiModelProperty("队列名字（如果msgType参数为MQ_QUEUE，则此参数必须传入；如果msgType参数不为MQ_QUEUE，则此参数不需要传入）")
    private String queueName;*/

    @ApiModelProperty("需要发送给具体Web Socket客户端的客户端ID（只有在msgType参数为WEBSOCKET的时候，此字段才有效；其它的时候忽略此字段。如果此参数为空，则向所有连接上的Web Socket客户端发送消息）")
    private Set<String> clientIds;

    @ApiModelProperty("手机号（如果msgType参数为SMS或LMS，则此参数必须传入；如果msgType参数不为SMS或LMS，则此参数不需要传入）")
    private String mobileNo;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Boolean getRetained() {
        return retained;
    }

    public void setRetained(Boolean retained) {
        this.retained = retained;
    }
/*

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
*/

    public Set<String> getClientIds() {
        return clientIds;
    }

    public void setClientIds(Set<String> clientIds) {
        this.clientIds = clientIds;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
