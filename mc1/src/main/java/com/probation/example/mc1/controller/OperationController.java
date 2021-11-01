package com.probation.example.mc1.controller;

import com.probation.example.mc1.serviceImpl.OperationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OperationController {

    private final OperationServiceImpl operationService;

    @Autowired
    public OperationController(OperationServiceImpl operationService) {
        this.operationService = operationService;
    }

    @GetMapping("/start")
    public String startService() {
        return operationService.startService();
    }

    @GetMapping("/stop")
    public String stopService() {
        return operationService.stopService();
    }
}
