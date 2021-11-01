package com.probation.example.mc1.helper;

import com.probation.example.mc1.controller.dto.MessageDto;

import java.util.ArrayList;
import java.util.List;

public class DataSenderComposite implements DataSender {
    private List<DataSender> dataSenders = new ArrayList<>();

    public void add(DataSender dataSender){
        this.dataSenders.add(dataSender);
    }

    @Override
    public void sendMessage(MessageDto messageDto) {
        for(DataSender dataSender : dataSenders) {
            dataSender.sendMessage(messageDto);
        }
    }
}
