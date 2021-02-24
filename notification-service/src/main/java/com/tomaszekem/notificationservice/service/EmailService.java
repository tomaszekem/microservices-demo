package com.tomaszekem.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
class EmailService {

    private final JavaMailSender mailSender;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailMessage(EmailNotificationDTO emailNotificationDTO) {
        try {
            SimpleMailMessage message = toSimpleMailMessage(emailNotificationDTO);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Could not send mail message {}. Reason: {}", emailNotificationDTO, e.getMessage());
        }
    }

    private SimpleMailMessage toSimpleMailMessage(EmailNotificationDTO emailNotificationDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailNotificationDTO.getTo());
        message.setSubject(emailNotificationDTO.getSubject());
        message.setText(emailNotificationDTO.getMessage());
        message.setFrom("from@acme.com");
        return message;
    }

}
