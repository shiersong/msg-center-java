package org.msg.msgcenter.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.msg.msgcenter.model.Message;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.msg.msgcenter.constant.Constant.DATE_FORMAT;

@Component
public class MQTTProducerService {

    @Autowired
    private MQTTProducer mqttProducer;

    public void produce(String topic, Object data) {
        Message message = new Message(data);
        mqttProducer.produce(topic, UTF8Buffer.utf8(JSON.toJSONStringWithDateFormat(message, DATE_FORMAT, SerializerFeature.WriteDateUseDateFormat)).data);
    }

    public void produce(String topic, boolean retained, Object data) {
        Message message = new Message(data);
        mqttProducer.produce(topic, retained, UTF8Buffer.utf8(JSON.toJSONStringWithDateFormat(message, DATE_FORMAT, SerializerFeature.WriteDateUseDateFormat)).data);
    }
}
