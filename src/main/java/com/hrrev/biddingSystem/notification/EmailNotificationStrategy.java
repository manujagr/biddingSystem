package com.hrrev.biddingSystem.notification;

import com.hrrev.biddingSystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("emailNotificationStrategy")
public class EmailNotificationStrategy implements NotificationStrategy {

    //private final JavaMailSender mailSender;

//    @Autowired
//    public EmailNotificationStrategy(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }

    @Override
    public void sendNotification(User user, NotificationMessage message) {
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(user.getEmail());
//        mailMessage.setSubject(message.getSubject());
//        mailMessage.setText(message.getContent());
//        mailSender.send(mailMessage);
        System.out.println("Mail Sent");
    }
}
