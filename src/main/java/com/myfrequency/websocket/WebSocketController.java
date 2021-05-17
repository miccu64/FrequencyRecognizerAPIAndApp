package com.myfrequency.websocket;

import com.myfrequency.models.FreqMagnModel;
import com.myfrequency.models.ResultModel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    public boolean connect() {
        return false;
    }

    //connected/sendData
    @MessageMapping("/sendData")
    @SendTo("/freqPlay/connected")
    public ResultModel result(FreqMagnModel message) {
        return new ResultModel(100.F,false);
    }
}
