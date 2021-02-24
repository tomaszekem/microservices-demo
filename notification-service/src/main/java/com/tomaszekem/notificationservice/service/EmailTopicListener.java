package com.tomaszekem.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class EmailTopicListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmailService emailService;

    EmailTopicListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = "greetings",
            groupId = "group")
    void listenForEmailsToSend(String messageDTOString) {
        log.info("Received email to send {} from queue", messageDTOString);
        try {
            EmailNotificationDTO notificationToSend = objectMapper.readValue(messageDTOString, EmailNotificationDTO.class);
            emailService.sendEmailMessage(notificationToSend);
        } catch (JsonProcessingException e) {
            log.error("Could not deserialize message {}", messageDTOString);
        }

    }
}
