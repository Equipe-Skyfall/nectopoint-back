package com.nectopoint.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    //Recebe um email(destinatário) e o código de verificação
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nectopointfatec@gmail.com");
        message.setTo(to);
        message.setSubject("Código de Verificação");
        message.setText("Seu código é: " + code + "\nEsse código expira em 10 minutos.");
        
        mailSender.send(message);
    }
}