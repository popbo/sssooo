package com.stdc.visual.ws;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

/**
 * @author: wang_jie
 * @data: 2023/3/30--10:09
 * @describe: 和组件事件进行通信的ws
 */
@Data
@Component
@ServerEndpoint("/ws/event")
@Slf4j
public class VisualWebSocket {

    //实例一个session，这个session是websocket的session
    private Session session;

    //存放websocket的集合（本次demo不会用到，聊天室的demo会用到）
    public static CopyOnWriteArraySet<VisualWebSocket> visualWebSockets = new CopyOnWriteArraySet<>();

    /**
     * 当前websocket分配线程池
     */
    private ExecutorService executors;

    //前端请求时一个websocket时
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        visualWebSockets.add(this);
        log.info("【visualWebSockets消息】有新的连接, 总数:{}", visualWebSockets.size());
    }

    //前端关闭时一个websocket时
    @OnClose
    public void onClose() {
        log.info("【websocket连接关闭】sessionId:"+this.session.getId());
        visualWebSockets.remove(this);
        try {
            this.session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("【visualWebSockets消息】连接断开, 总数:{}", visualWebSockets.size());
    }

    //前端向后端发送消息
    @OnMessage
    public void onMessage(String message) {

        log.info("【visualWebSockets消息】收到客户端发来的消息:{}", message);
    }

    public synchronized void setMessage(Object message){
        if (message instanceof String){
            setMessage(message);
        }else {
            setMessage(JSON.toJSONString(message));
        }
    }

    private void setMessage(String message){
        try {
            if (this.session.isOpen()){
                log.info("【websocket推送新消息】sessionId:{},\n消息:{},", this.session.getId(),message);
                this.session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //新增一个方法用于主动向客户端发送消息
    public static void sendMessage(String message) {
        for (VisualWebSocket visualWebSocket : visualWebSockets) {
            log.info("【visualWebSockets消息】广播消息, message={}", message);
            try {
                visualWebSocket.session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //新增一个方法用于主动向客户端发送消息
    public static void sendMessage(Object message) {
        sendMessage(JSON.toJSONString(message));
    }
    
    
}
