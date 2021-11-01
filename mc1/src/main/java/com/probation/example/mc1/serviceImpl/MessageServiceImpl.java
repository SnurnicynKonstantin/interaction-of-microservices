package com.probation.example.mc1.serviceImpl;

import com.probation.example.mc1.controller.dto.MessageDto;
import com.probation.example.mc1.model.Message;
import com.probation.example.mc1.repository.MessageRepository;
import com.probation.example.mc1.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class MessageServiceImpl implements MessageService {

    private static final String UPDATED_TEXT = "Message updated successfully";
    private static final String NOT_FOUND_TEXT = "Message not found successfully";

    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public String updateMessage(Long id, MessageDto messageDto) {
        Optional<Message> messageFromDb = messageRepository.findById(id);
        Message messageFromUpdate = new Message();
        messageFromDb.ifPresent(message -> {
            messageFromUpdate.setId(message.getId());
            messageFromUpdate.setSessionId(message.getSessionId());
            messageFromUpdate.setMc1Timestamp(message.getMc1Timestamp());
            messageFromUpdate.setMc2Timestamp(messageDto.getMc2Timestamp());
            messageFromUpdate.setMc3Timestamp(messageDto.getMc3Timestamp());
            messageFromUpdate.setEndTimestamp(LocalDateTime.now());
        });

        messageRepository.save(messageFromUpdate);
        return messageFromDb.isPresent() ? UPDATED_TEXT : NOT_FOUND_TEXT;
    }

    @Override
    public Message createMessage() {
        Random rand = new Random();
        Integer sessionId = rand.nextInt(Integer.MAX_VALUE);
        Message message = new Message();
        message.setSessionId(sessionId);
        message.setMc1Timestamp(LocalDateTime.now());
        messageRepository.save(message);
        return message;
    }
}
