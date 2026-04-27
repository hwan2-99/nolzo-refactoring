package com.noljo.nolzo.notification.adapter.out.email;

import com.noljo.nolzo.notification.application.port.out.SendEmailNotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingEmailNotificationAdapter implements SendEmailNotificationPort {

    @Override
    public void send(String recipient, String subject, String body) {
        // TODO: 실제 메일 서버(SMTP, SES 등) 연동 시 이 지점을 실제 발송 로직으로 교체한다.
        log.info("Email notification sent. recipient={}, subject={}, body={}", recipient, subject, body);
    }
}
