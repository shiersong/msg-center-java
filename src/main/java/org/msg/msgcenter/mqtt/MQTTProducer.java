package org.msg.msgcenter.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * @author Scott
 */
@MessagingGateway(defaultRequestChannel = "outboundChannel")
public interface MQTTProducer {

    void produce(@Header(MqttHeaders.TOPIC) String topic, Object message);

    void produce(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.RETAINED) boolean retained, Object message);

}