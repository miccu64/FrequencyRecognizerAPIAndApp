package com.myfrequency.websocket;

import com.myfrequency.models.FreqMagnModel;
import com.myfrequency.models.ResultModel;
import com.myfrequency.models.TransportSingleton;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class WebSocketController {

    //connected/sendData
    @MessageMapping("/sendData")
    @SendTo("/freqPlay/connected")
    public ResultModel result(FreqMagnModel message) {
        //update values in transport singleton
        TransportSingleton.getInstance().model = message;
        return new ResultModel(100.F,false);
    }

    @MessageMapping("/hello")
    @SendTo("/freqPlay/connected")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
/*
    @MessageMapping("/sendData")
    @SendTo("/freqPlay/connected")
    public ResultModel result(String message) {
        //update values in transport singleton
        System.out.println(message);
        //TransportSingleton.getInstance().model = message;
        return new ResultModel(100.F,false);
    }*/

}
