package com.probation.example.mc1.service;

import com.probation.example.mc1.controller.dto.MessageDto;
import com.probation.example.mc1.model.Message;

public interface MessageService {
    String updateMessage(Long id, MessageDto messageDto);
    Message createMessage();
}
