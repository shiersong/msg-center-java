package org.msg.msgcenter.controller;

import org.msg.msgcenter.httprequest.MsgControllerSendMsgReq;
import org.msg.msgcenter.mqtt.MQTTProducerService;
import org.msg.msgcenter.utils.BindingResultUtil;
import org.msg.msgcenter.ws.WebSocketEndPoint;
import org.msg.msgcenter.httpresponse.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.LinkedHashMap;
import java.util.Map;

@Api(tags = "MsgController", description = "消息中心相关接口")
@RestController
@RequestMapping("/msgPublisher")
public class MsgController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MsgController.class);

    @Value("${websocket.innerTopic}")
    private String websocketInnerTopic;

    @Autowired
    private MQTTProducerService mqttProducerService;

    @Autowired
    private WebSocketEndPoint webSocketEndPoint;

    @ApiOperation(value = "发送消息")
    @PostMapping("/sendMsg")
    public AjaxResult sendMsg(@RequestBody @Valid MsgControllerSendMsgReq msgControllerSendMsgReq, BindingResult bindingResult) {
        try {
            String errors = BindingResultUtil.toString(bindingResult, "，");
            if (! StringUtils.trimToEmpty(errors).equals("")) {
                return AjaxResult.error(StringUtils.trimToEmpty(errors));
            }

            switch (StringUtils.trimToEmpty(msgControllerSendMsgReq.getMsgType())) {
                case "MQTT": {
                    if (StringUtils.trimToEmpty(msgControllerSendMsgReq.getTopic()).equals("")) {
                        return AjaxResult.error("请指定Topic");
                    }
                    if (msgControllerSendMsgReq.getRetained() != null) {
                        mqttProducerService.produce(StringUtils.trimToEmpty(msgControllerSendMsgReq.getTopic()), msgControllerSendMsgReq.getRetained(), msgControllerSendMsgReq.getMessage());
                    }
                    else {
                        mqttProducerService.produce(StringUtils.trimToEmpty(msgControllerSendMsgReq.getTopic()), msgControllerSendMsgReq.getMessage());
                    }
                    break;
                }
                case "WEBSOCKET": {
                    Map<String, Object> innerWebSocketMsg = new LinkedHashMap<>();
                    innerWebSocketMsg.put("clientIds", msgControllerSendMsgReq.getClientIds());
                    innerWebSocketMsg.put("message", msgControllerSendMsgReq.getMessage());
                    mqttProducerService.produce(this.websocketInnerTopic, innerWebSocketMsg);
                    break;
                }
                case "SMS": {
                    //return AjaxResult.error("暂时还不支持短信，后期会支持，请持续关注");
                    break;
                }
                case "LMS": {
                    //return AjaxResult.error("暂时还不支持长短信，后期会支持，请持续关注");
                    break;
                }
                default: {
                    return AjaxResult.error("消息类型不存在");
                }
            }
            return AjaxResult.success();
        }
        catch (Throwable t) {
            LOGGER.error("发生异常：", t);
            return AjaxResult.error("发送消息失败");
        }
    }
}
