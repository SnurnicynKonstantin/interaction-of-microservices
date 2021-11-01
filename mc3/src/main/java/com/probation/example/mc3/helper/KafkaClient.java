package com.probation.example.mc3.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.probation.example.mc3.dto.MessageDto;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class KafkaClient {

    private final RestTemplate restTemplate;
    private final String microcontrollerHost;
    private final Tracer tracer;

    public KafkaClient(RestTemplate restTemplate, @Value("${mc1.endpoint}") String microcontrollerHost, Tracer tracer) {
        this.restTemplate = restTemplate;
        this.microcontrollerHost = microcontrollerHost;
        this.tracer = tracer;
    }

    @KafkaListener(
            topics = "message",
            groupId = "mc2")
    void commonListenerForMultipleTopics(String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject = new JSONObject(message);
        MessageDto receivedMessage = objectMapper.readValue(jsonObject.toString(), MessageDto.class);
        receivedMessage.setMc3Timestamp(LocalDateTime.now());
        sendRequest(receivedMessage);
        sendSpan(receivedMessage);
    }

    private void sendRequest(MessageDto message) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MessageDto> entity = new HttpEntity<MessageDto>(message, httpHeaders);
        restTemplate.exchange(microcontrollerHost + message.getId(), HttpMethod.PUT, entity, String.class);
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
}
