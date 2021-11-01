package com.probation.example.mc1.helper;

import com.probation.example.mc1.controller.dto.MessageDto;

public interface DataSender {
    void sendMessage(MessageDto messageDto);
}
