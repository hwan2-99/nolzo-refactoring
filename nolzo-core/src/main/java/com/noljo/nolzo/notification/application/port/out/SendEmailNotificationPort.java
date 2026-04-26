package com.noljo.nolzo.notification.application.port.out;

public interface SendEmailNotificationPort {

    void send(String recipient, String subject, String body);
}
