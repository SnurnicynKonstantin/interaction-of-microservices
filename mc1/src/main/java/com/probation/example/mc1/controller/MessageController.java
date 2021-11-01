package com.probation.example.mc1.controller;

import com.probation.example.mc1.controller.dto.MessageDto;
import com.probation.example.mc1.serviceImpl.MessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageController {

    private static final String CREATED_TEXT = "Message created successfully";

    private final MessageServiceImpl messageService;

    @Autowired
    public MessageController(MessageServiceImpl messageService) {
        this.messageService = messageService;
    }

    @PutMapping("{id}")
    public String updateMessage(@PathVariable Long id,
                                @RequestBody MessageDto messageDto) {
        return messageService.updateMessage(id, messageDto);
    }

    @PostMapping
    public String createMessage() {
        messageService.createMessage();
        return CREATED_TEXT;
    }
}
