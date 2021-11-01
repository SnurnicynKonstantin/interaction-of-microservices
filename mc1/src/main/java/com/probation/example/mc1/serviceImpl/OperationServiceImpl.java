package com.probation.example.mc1.serviceImpl;


import com.probation.example.mc1.helper.ServiceDataSender;
import com.probation.example.mc1.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

import static com.probation.example.mc1.dictionaries.Answer.*;

@Service
public class OperationServiceImpl implements OperationService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ServiceDataSender serviceDataSender;

    @Autowired
    public OperationServiceImpl(ServiceDataSender serviceDataSender) {
        this.serviceDataSender = serviceDataSender;
    }

    @Override
    public String startService() {
        String answer;
        if(serviceDataSender.isLaunched()) {
            answer = ALREADY_STARTED_TEXT.getDescription();
        } else {
            executor.execute(serviceDataSender);
            answer = STARTED_TEXT.getDescription();
        }
        return answer;
    }

    @Override
    public String stopService() {
        String answer;
        serviceDataSender.stopSendingMessage();
        answer = STOP_TEXT.getDescription();
        return answer;
    }
}
