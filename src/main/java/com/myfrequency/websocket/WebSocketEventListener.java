package com.myfrequency.websocket;

import com.myfrequency.models.TransportSingleton;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

//change the count if conn/disconn
@Component
public class WebSocketEventListener {

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        TransportSingleton.getInstance().usersCount++;
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        TransportSingleton.getInstance().usersCount--;
    }
}