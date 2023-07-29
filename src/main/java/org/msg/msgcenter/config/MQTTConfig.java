package org.msg.msgcenter.config;

import org.msg.msgcenter.mqtt.MqttCallbackHandle;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.UUID;

@Configuration
public class MQTTConfig {

    @Value("${mqtt.user}")
    private String user;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.host}")
    private String host;

    @Value("${mqtt.port}")
    private Integer port;

    @Value("${mqtt.keepAliveInterval}")
    private Integer keepAliveInterval;

    @Value("${mqtt.connectionTimeout}")
    private Integer connectionTimeout;

    @Value("${mqtt.clientId}")
    private String clientId;

    @Value("${mqtt.producer.defaultRetained}")
    private Boolean defaultRetained;

    @Value("${mqtt.producer.defaultQos}")
    private Integer defaultProducerQos;

    @Value("${websocket.defaultQos}")
    private Integer websocketDefaultQos;

    @Value("${websocket.innerTopic}")
    private String websocketInnerTopic;

    @Value("${websocket.completionTimeout}")
    private Long completionTimeout;

    @Autowired
    private MqttCallbackHandle mqttCallbackHandle;

    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(user);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[] {"tcp://" + host + ":" + port});
        mqttConnectOptions.setKeepAliveInterval(keepAliveInterval);
        mqttConnectOptions.setConnectionTimeout(connectionTimeout);

        return mqttConnectOptions;
    }

    @Bean
    public MqttPahoClientFactory getMqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());

        return factory;
    }

    /**
     * 生产者的消息发送通道
     * @return
     */
    @Bean
    public MessageChannel outboundChannel() {
        return new DirectChannel();
    }

    /**
     * 生产者
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "outboundChannel")
    public MessageHandler getMqttProducer() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, getMqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultRetained(defaultRetained);
        messageHandler.setDefaultQos(defaultProducerQos);

        return messageHandler;
    }

    /**
     * MQTT消息订阅绑定
     * @return
     */
    @Bean
    public MessageProducer inbound() {
        // 可以同时消费（订阅）多个Topic
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                UUID.randomUUID().toString(), getMqttClientFactory());

        adapter.addTopic(this.websocketInnerTopic, this.websocketDefaultQos);

        adapter.setCompletionTimeout(this.completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(this.websocketDefaultQos);
        // 设置订阅通道
        adapter.setOutputChannel(mqttInBoundChannel());
        return adapter;
    }

    /**
     * MQTT信息通道（消费者）
     * @return
     */
    @Bean(name = "inboundChannel")
    public MessageChannel mqttInBoundChannel(){
        return new DirectChannel();
    }

    /**
     *  MQTT消息处理器（消费者）
     *  ServiceActivator注解表明当前方法用于处理MQTT消息，inputChannel参数指定了用于接收消息信息的channel
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "inboundChannel")
    public MessageHandler handler() {
        return message -> {
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
            String payload = message.getPayload().toString();
            mqttCallbackHandle.handle(topic, payload);
        };
    }
}
