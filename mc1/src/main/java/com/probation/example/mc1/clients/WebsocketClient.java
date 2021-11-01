package com.probation.example.mc1.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.probation.example.mc1.controller.dto.MessageDto;
import com.probation.example.mc1.helper.DataSender;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebsocketClient implements DataSender {

    private final URI endpointURI;

    Session userSession = null;
    private MessageHandler messageHandler;

    public WebsocketClient(URI endpointURI) {
        this.endpointURI = endpointURI;
    }

    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    @Override
    public void sendMessage(MessageDto messageDto) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(messageDto);
            this.userSession.getAsyncRemote().sendText(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static interface MessageHandler {
        public void handleMessage(String message);
    }
}