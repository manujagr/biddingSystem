package com.intuit.biddingSystem.notification.service;


import com.intuit.biddingSystem.notification.NotificationMessage;
import com.intuit.biddingSystem.user.model.User;
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
        String email = user.getEmail();
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(user.getEmail());
//        mailMessage.setSubject(message.getSubject());
//        mailMessage.setText(message.getContent());
//        mailSender.send(mailMessage);
        System.out.println("Mail Sent to userId: " + user.getUserId());
    }
}
