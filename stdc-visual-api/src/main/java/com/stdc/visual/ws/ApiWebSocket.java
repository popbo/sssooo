package com.stdc.visual.ws;

import com.alibaba.fastjson.JSON;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2023/3/30--11:12
 * @describe: 提供给第三方的ws连接, apiId 使用前端传来的 eventId
 */
@Data
@Component
@ServerEndpoint("/api/{apiId}")
@Slf4j
public class ApiWebSocket {

    private String apiId;

    //实例一个session，这个session是websocket的session
    private Session session;

    //存放websocket的集合（本次demo不会用到，聊天室的demo会用到）
    public static CopyOnWriteArraySet<ApiWebSocket> apiWebSockets = new CopyOnWriteArraySet<>();

    /**
     * 当前websocket分配线程池
     */
    private ExecutorService executors;

    //前端请求时一个websocket时
    @OnOpen
    public void onOpen(@PathParam("apiId") String apiId, Session session) {
        ApiWebSocket apiWs = getApiWs(apiId);
        if (ObjectUtil.isNotEmpty(apiWs)) apiWs.onClose();
        this.session = session;
        this.apiId = apiId;
        apiWebSockets.add(this);
        log.info("【apiWebSockets消息】有新的连接,apiID:"+apiId+",sessionId:"+this.session.getId()+", 总数:{}", apiWebSockets.size());
    }

    //前端关闭时一个websocket时
    @OnClose
    public void onClose() {
        apiWebSockets.remove(this);
        try {
            this.session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("【apiWebSockets消息】连接断开,apiID:"+apiId+",sessionId:"+this.session.getId()+", 总数:{}", apiWebSockets.size());
    }

    //前端向后端发送消息
    @OnMessage
    public void onMessage(String message) {
        VisualWebSocket.sendMessage(message);
        log.info("【apiWebSockets消息】,apiID:"+apiId+",收到客户端发来的消息:{}", message);
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
                log.info("【websocket推送新消息】,apiID:"+apiId+",sessionId:{},\n消息:{},", this.session.getId(),message);
                this.session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //新增一个方法用于主动向客户端发送消息
    public static void sendMessage(String message) {
        for (ApiWebSocket apiWebSocket : apiWebSockets) {
            log.info("【apiWebSockets消息】,apiID:"+apiWebSocket.apiId+",广播消息, message={}", message);
            try {
                apiWebSocket.session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //获取对应apiId的ws对象
    public static ApiWebSocket getApiWs(String apiId){
        List<ApiWebSocket> sockets = apiWebSockets.stream().filter(apiWebSocket -> StringUtil.equals(apiId, apiWebSocket.apiId)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sockets)) return null;
        return sockets.get(0);
    }


}
