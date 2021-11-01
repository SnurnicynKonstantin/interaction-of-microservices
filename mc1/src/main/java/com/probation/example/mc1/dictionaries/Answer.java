package com.probation.example.mc1.dictionaries;

public enum Answer {

    STARTED_TEXT("Service started"),
    ALREADY_STARTED_TEXT("Service already started"),
    STOP_TEXT("Service was stopped");

    private final String description;

    Answer(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}