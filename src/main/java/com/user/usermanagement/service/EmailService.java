package com.user.usermanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmailId, String subject, String token){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setTo(toEmailId);
        message.setSubject(subject);
        String passwordResetLink = "http://localhost:8080/auth/reset?token="+token;
        message.setText("Click on the link to reset your password: " + passwordResetLink);
        mailSender.send(message);

    }
}
