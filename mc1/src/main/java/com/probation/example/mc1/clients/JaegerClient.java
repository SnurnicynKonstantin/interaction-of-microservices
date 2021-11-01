package com.probation.example.mc1.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.probation.example.mc1.controller.dto.MessageDto;
import com.probation.example.mc1.helper.DataSender;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

import java.util.Collections;

public class JaegerClient implements DataSender {

    private final Tracer tracer;

    public JaegerClient(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void sendMessage(MessageDto messageDto) {
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
