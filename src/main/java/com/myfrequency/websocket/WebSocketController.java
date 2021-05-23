package com.myfrequency.websocket;

import com.myfrequency.models.FreqMagnModel;
import com.myfrequency.models.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Observable;

@Controller
public class WebSocketController extends Observable {
    @Autowired
    private SimpMessagingTemplate template;

    //connected/sendData
    @MessageMapping("/sendData")
    @SendTo("/freqPlay/connected")
    public void result(FreqMagnModel message) {
        //update values in transport singleton
        setChanged();
        notifyObservers(message);
    }

    //send recognized result
    @SendTo("/freqPlay/connected")
    public ResultModel sendResult(ResultModel res) {
        template.convertAndSend("/freqPlay/connected", res);
        return res;
    }
}
