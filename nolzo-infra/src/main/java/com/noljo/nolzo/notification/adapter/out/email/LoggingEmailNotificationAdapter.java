package com.noljo.nolzo.notification.adapter.out.email;

import com.noljo.nolzo.notification.application.port.out.SendEmailNotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingEmailNotificationAdapter implements SendEmailNotificationPort {

    @Override
    public void send(String recipient, String subject, String body) {
        log.info("Email notification sent. recipient={}, subject={}, body={}", recipient, subject, body);
    }
}
