package com.joeshaw.websocketdemo.controller;

import com.joeshaw.websocketdemo.util.WebSocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import sun.rmi.runtime.Log;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Controller
@ServerEndpoint("/chat/{username}") //说明创建websocket的endpoint
public class WebSocketController {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketController.class);

    /**
     * 访问聊天室页面
     * @return
     */
    @GetMapping("/chatPage")
    public String chatPage(){
        return "chat";
    }


    @OnOpen
    public void openSession(@PathParam("username") String username, Session session){
        //存储用户
        WebSocketUtil.USERS_ONLINE.put(username,session);
        //向所有用户发送上线通知消息
        String message = "["+username+"]进入聊天室";
        LOG.info(message);
        WebSocketUtil.sendMessageToAllOnlineUser(message);
    }

    @OnClose
    public void closeSession(@PathParam("username") String username, Session session){
        //删除用户
        WebSocketUtil.USERS_ONLINE.remove(username);
        //向所有用户发送下线通知消息
        String message = "["+username+"]离开了聊天室";
        LOG.info(message);
        WebSocketUtil.sendMessageToAllOnlineUser(message);

    }


    @OnMessage
    public void onMessage(@PathParam("username") String username, String message){
        //向聊天室中的人发送消息
        message = "["+username+"]：" + message;
        LOG.info(message);
        WebSocketUtil.sendMessageToAllOnlineUser(message);
    }


    @OnError
    public void sessionError(Session session, Throwable throwable){
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.error("WebSocket连接发生异常，message:"+throwable.getMessage());


    }

}
