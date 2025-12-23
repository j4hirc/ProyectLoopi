package com.example.demo.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // CONFIGURACIÓN PARA PUERTO 587 (STANDARD MODERNO)
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587); // CAMBIO IMPORTANTE
        
        // OJO: Si usas variables de entorno, asegúrate que lean esto, 
        // pero como lo estás poniendo "hardcoded", esto mandará.
        mailSender.setUsername("juanalberto25423@gmail.com");
        mailSender.setPassword("lxdjgcnixgknpfvu"); 

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        
        // AQUÍ ESTÁ EL TRUCO PARA QUE PASE EL FIREWALL DE LA NUBE:
        props.put("mail.smtp.starttls.enable", "true");  // PRENDIDO
        props.put("mail.smtp.starttls.required", "true"); // OBLIGATORIO
        props.put("mail.smtp.ssl.enable", "false");      // APAGADO (Esto causa el timeout en 465)
        
        props.put("mail.debug", "true"); 
        
        return mailSender;
    }
}