package com.myfrequency.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

//change the count if conn/disconn
@Component
public class WebSocketEventListener {
    private int usersCount;

    public int getUsersCount() {
        return usersCount;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        usersCount++;
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        usersCount--;
    }
}