package com.probation.example.mc2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.probation.example.mc2.controller.dto.MessageDto;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

public class MessageHandler extends TextWebSocketHandler {

    private final Producer kafkaProducer;
    private final String kafkaTopic;
    private final Tracer tracer;

    public MessageHandler(Producer kafkaProducer, String kafkaTopic, Tracer tracer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaTopic = kafkaTopic;
        this.tracer = tracer;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject = new JSONObject(message.getPayload());
        MessageDto receivedMessage = objectMapper.readValue(jsonObject.toString(), MessageDto.class);
        produceToKafka(receivedMessage);
        sendSpan(receivedMessage);
    }

    private void produceToKafka(MessageDto receivedMessage) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(convertReceivedMessageToSend(receivedMessage));
            kafkaProducer.send(new ProducerRecord<>(kafkaTopic, receivedMessage.getSessionId().toString(), message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendSpan(MessageDto messageDto) {
        ObjectMapper mapper = new ObjectMapper();
        try (Scope scope = tracer.buildSpan("sessionId = " + messageDto.getSessionId()).startActive(true)) {
            try {
                String message = mapper.writeValueAsString(messageDto);
                Span span = scope.span();
                span.log(Collections.singletonMap("message", message));
                span.setTag("sessionId", messageDto.getSessionId());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private MessageDto convertReceivedMessageToSend(MessageDto receivedMessage) {
        MessageDto messageToSend = new MessageDto();
        messageToSend.setId(receivedMessage.getId());
        messageToSend.setSessionId(receivedMessage.getSessionId());
        messageToSend.setMc1Timestamp(receivedMessage.getMc1Timestamp());
        messageToSend.setMc2Timestamp(LocalDateTime.now());
        return messageToSend;
    }

}