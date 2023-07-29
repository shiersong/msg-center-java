package org.msg.msgcenter.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.msg.msgcenter.model.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.msg.msgcenter.constant.Constant.DATE_FORMAT;

@Component
@ServerEndpoint(value = "/ws/{clientId}")
public class WebSocketEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEndPoint.class);

    private static Map<String, CopyOnWriteArraySet<Session>> webSocketSessionMap = new Hashtable<>();

    private static int sessionCount;

    private static long maxIdleTimeout;

    @OnOpen
    public void onOpen(Session session, @PathParam("clientId") String clientId){
        open(session, clientId);
    }

    public void open(Session session, String clientId){
        if (webSocketSessionMap.get(clientId) == null) {
            webSocketSessionMap.put(clientId, new CopyOnWriteArraySet<Session>());
        }
        if (webSocketSessionMap.get(clientId).contains(session)) {
            return;
        }
        session.setMaxIdleTimeout(maxIdleTimeout);
        webSocketSessionMap.get(clientId).add(session);
        sessionCount++;
        LOGGER.info("clientId为{}的客户端连接成功", clientId);
        LOGGER.info("正在连接的客户端数量是：{}", sessionCount);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("clientId") String clientId) {
        receiveMessage(message, clientId);
    }
    public void receiveMessage(String message, String clientId) {
        LOGGER.info("clientId为{}的客户端发送过来的消息是：{}", clientId, message);
        Map<String, Object> heartBeatMsg = new HashMap<>();
        heartBeatMsg.put("code", 0);
        heartBeatMsg.put("msg", "OK");
        heartBeatMsg.put("heartBeatTime", new Date());
        sendMsg(heartBeatMsg, clientId);
    }

    @OnClose
    public void onClose(Session session, @PathParam("clientId") String clientId){
        close(session, clientId);
    }

    public void close(Session session, String clientId){
        try {
            LOGGER.info("关闭clientId为{}、session id为[{}]的连接", clientId, session.getId());
            if (session != null && session.isOpen()) {
                session.close();
            }
            sessionCount--;
            Set<Session> sessions = webSocketSessionMap.get(clientId);
            if (sessions != null && sessions.contains(session)) {
                sessions.remove(session);
            }
            LOGGER.info("正在连接的客户端数量是：{}", sessionCount);
        } catch (Throwable throwable) {
            LOGGER.error("发生异常：", throwable);
        }
    }

    @OnError
    public void onError(Throwable throwable,Session session){
        LOGGER.error("发生异常：", throwable);
        LOGGER.info("正在连接的客户端数量是：{}", sessionCount);
    }

    public void sendMsg(Object messageObject){
        Iterator<String> iterator = webSocketSessionMap.keySet().iterator();
        while (iterator.hasNext()) {
            String clientId = iterator.next();
            sendMsg(messageObject, clientId);
        }
    }

    public void sendMsg(Object messageObject, String clientId){
        Message message = new Message(messageObject);
        if (! StringUtils.trimToEmpty(clientId).equals("")) {
            Set<Session> sessions = webSocketSessionMap.get(clientId);
            if (sessions != null && sessions.size() > 0) {
                Iterator<Session> sessionIterator = sessions.iterator();
                while (sessionIterator.hasNext()) {
                    Session session = sessionIterator.next();
                    if (session != null && session.isOpen()) {
                        String msg = JSON.toJSONStringWithDateFormat(message, DATE_FORMAT, SerializerFeature.WriteDateUseDateFormat);
                        LOGGER.info("向{}的客户端发送的消息是：{}", clientId, msg);
                        synchronized (session) {
                            try {
                                session.getBasicRemote().sendText(msg, true);
                            } catch (Throwable throwable) {
                                LOGGER.error("发生异常：", throwable);
                            }
                        }
                    }
                }
            }
        }
    }

    public void sendMsg(Object messageObject, Set<String> clientIds){
        if (clientIds != null && clientIds.size() > 0) {
            Iterator<String> userIdIterator = clientIds.iterator();
            while (userIdIterator.hasNext()) {
                String clientId = userIdIterator.next();
                sendMsg(messageObject, clientId);
            }
        }
    }

    @Value("${websocket.maxIdleTimeout}")
    public void setMaxIdleTimeout(long maxIdleTimeout) {
        WebSocketEndPoint.maxIdleTimeout = maxIdleTimeout;
    }
}