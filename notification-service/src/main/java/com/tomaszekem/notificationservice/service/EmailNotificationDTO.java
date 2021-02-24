package com.tomaszekem.notificationservice.service;

public class EmailNotificationDTO {
    private String to;
    private String subject;
    private String message;

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SendEmailNotificationCommand{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

