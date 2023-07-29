package org.msg.msgcenter.mqtt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.msg.msgcenter.ws.WebSocketEndPoint;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

@Component
public class MqttCallbackHandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttCallbackHandle.class);

    @Value("${websocket.innerTopic}")
    private String websocketInnerTopic;

    @Autowired
    private WebSocketEndPoint webSocketEndPoint;

    /**
     * 接收消息处理
     * @param topic
     * @param payload
     */
    public void handle(String topic, String payload){
        LOGGER.info("MqttCallbackHandle:" + topic + "---"+ payload);

        // 根据topic分别进行消息处理。
        if (topic.endsWith(this.websocketInnerTopic)){
            //Web Socket内部消息
            JSONObject jsonObject = JSONObject.parseObject(payload);
            JSONObject data = jsonObject.getJSONObject("data");
            if (! CollectionUtils.sizeIsEmpty(data.get("clientIds"))) {
                JSONArray clientIds = data.getJSONArray("clientIds");
                this.webSocketEndPoint.sendMsg(data.get("message"), new LinkedHashSet<>(clientIds.toJavaList(String.class)));
            }
            else {
                this.webSocketEndPoint.sendMsg(data.get("message"));
            }
        }
    }
}
