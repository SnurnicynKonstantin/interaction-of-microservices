package com.probation.example.mc1.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.probation.example.mc1.clients.JaegerClient;
import com.probation.example.mc1.clients.WebsocketClient;
import com.probation.example.mc1.controller.dto.MessageDto;
import com.probation.example.mc1.model.Message;
import com.probation.example.mc1.service.MessageService;

import java.util.Date;

public class ServiceDataSender implements Runnable  {

    private final Long runningTimeDuration;
    private final WebsocketClient websocketClient;
    private final MessageService messageService;
    private final Integer runningDelayTime;
    private final DataSenderComposite dataSenderComposite;
    
    private Boolean launched = false;
    private long secondsLaunched;
    private int messageCounter = 0;

    public ServiceDataSender(Long runningTimeDuration, Integer runningDelayTime, WebsocketClient websocketClient,
                             MessageService messageService, DataSenderComposite dataSenderComposite) {
        this.runningTimeDuration = runningTimeDuration;
        this.websocketClient = websocketClient;
        this.messageService = messageService;
        this.runningDelayTime = runningDelayTime;
        this.dataSenderComposite = dataSenderComposite;
    }

    public void run() {
        launched = true;
        System.out.println("Process of sending message was started...");

        Date startDate = new Date();

        while (launched) {
            websocketClient.connect();
                    MessageDto messageToSend = generateMessage();
                    dataSenderComposite.sendMessage(messageToSend);
                    messageCounter++;

                secondsLaunched = (new Date().getTime() - startDate.getTime())/1000;
                if (runningTimeDuration < secondsLaunched) {
                    stopSendingMessage();
                }
            try {
                Thread.sleep(runningDelayTime); //For watch developer result
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Boolean isLaunched() {
        return launched;
    }

    public void stopSendingMessage() {
        this.launched = false;
        System.out.println("Sending of message was stopped.");
        System.out.println("Messages were sent within " + secondsLaunched + " seconds");
        System.out.println("Was sent " + messageCounter + " messages.");
        System.out.println("------------------------------------------------");
        this.messageCounter = 0;
    }

    private MessageDto generateMessage() {
        MessageDto messageToSend = new MessageDto();
        Message newMessage = messageService.createMessage();
        messageToSend.setId(newMessage.getId());
        messageToSend.setSessionId(newMessage.getSessionId());
        messageToSend.setMc1Timestamp(newMessage.getMc1Timestamp());
        return messageToSend;
    }

}
