package com.gpr.gotegameserver.websocket;

import com.gpr.gotegameserver.LamportClock;
import com.gpr.gotegameserver.InputLagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;

import java.util.Map;

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private final InputLagService inputLagService;

    private final SimpMessagingTemplate template;

    public WebSocketController(InputLagService inputLagService, SimpMessagingTemplate simpMessagingTemplate) {
        this.inputLagService = inputLagService;
        this.template = simpMessagingTemplate;
    }

    @MessageMapping("/input-lag")
    public void saveInputLag(Map<String, Object> payload) {
        inputLagService.register((Long) payload.get("sentTimestamp"));
        this.template.convertAndSend("/topic/message", new TextMessage(LamportClock.nowWithReference().toString()));
    }
}
