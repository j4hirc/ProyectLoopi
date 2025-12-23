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
        // 1. FORZAMOS IPV4 AQUÍ TAMBIÉN POR SI ACASO
        System.setProperty("java.net.preferIPv4Stack", "true");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // 2. CONFIGURACIÓN DURO (Hardcoded)
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(465);
        mailSender.setUsername("juanalberto25423@gmail.com");
        mailSender.setPassword("lxdjgcnixgknpfvu"); // Tu clave de aplicación

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp"); // Usamos smtp normal pero con SSL
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true"); // ESTO ACTIVA EL PUERTO 465
        props.put("mail.smtp.starttls.enable", "false"); // ESTO APAGA EL CONFLICTO
        props.put("mail.debug", "true"); // Para ver logs detallados si falla

        // Timeouts para que no se quede colgado
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return mailSender;
    }
}