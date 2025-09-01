package com.sky.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
// 声明一个WebSocket端点，每当客户端通过ws://localhost:8080/ws/xxx连接时，都由这个类来处理
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {

    // 存放会话对象
    private static Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 建立连接成功后调用该方法
     * @param session
     * @param sid
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("客户端{}建立连接", sid);
        sessionMap.put(sid, session);
    }

    /**
     * 关闭连接后调用该方法
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("连接{}断开", sid);
        sessionMap.remove(sid);
    }

    /**
     * 收到客户端消息后发送的方法
     * @param message
     * @param sid
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到客户端消息");
    }


    /**
     * 群发消息到客户端
     * @param message
     */
    public void sendToClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
