package com.github.bael.otus.java.domain;

import com.github.bael.otus.java.rest.dto.EventNotificationRequest;
import com.github.bael.otus.java.rest.dto.VotingNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

//    @Autowired
//    public void setMailSender(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }

    private final JavaMailSender mailSender;


    // todo docker run -d -p 8025:8025 -p 1025:1025 mailhog/mailhog
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendVotingNotification(VotingNotificationRequest request) {
        // TODO: Отправить HTML-письмо с кнопками/ссылками на слоты

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Здравствуйте ").append(request.recipient().fullName()).append(" \n");
        messageBuilder.append("Выберите слот для голосования из доступных: ").append("\n\n");
        request.slots()
                .forEach(slot -> {
                    messageBuilder.append("Время слота:  ").append(slot.startTime()).append(" - ").append(slot.endTime())
                            .append("\t\t").append(" Ссылка: ").append(slot.callbackUrl()).append("\n");
        });

        sendSimpleMessage(request.recipient().email(), "Голосование о встрече " + request.pollTitle(),
                messageBuilder.toString());
    }

    public void sendEventNotification(EventNotificationRequest request) {
        log.info("Отправляем сообщение  для {} о {}", request.recipient().fullName(), request.message());
        // TODO: Отправить уведомление (Telegram)
        sendSimpleMessage(request.recipient().email(),
                request.eventTime() + " " + request.eventType(),
                request.message());
    }
}