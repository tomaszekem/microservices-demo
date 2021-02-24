package com.tomaszekem.userservice.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

import static java.lang.String.format;

@Service
public class NotificationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static String EMAIL_TOPIC = "greetings";
    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaTemplate<String, SendEmailNotificationCommand> kafkaTemplate;

    public NotificationService(ApplicationEventPublisher applicationEventPublisher,
                               KafkaTemplate<String, SendEmailNotificationCommand> kafkaTemplate) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void queueEmailNotifications(List<SendEmailNotificationCommand> commands) {
        log.info("Queuing emails to send: {}. Emails will be sent after transaction is committed.", commands);
        commands.forEach(applicationEventPublisher::publishEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendEmailNotifications(SendEmailNotificationCommand sendEmailNotificationCommand) {
        log.info(format("Sending message %s to topic %s", sendEmailNotificationCommand, EMAIL_TOPIC));
        kafkaTemplate.send(EMAIL_TOPIC, sendEmailNotificationCommand);
    }

}
