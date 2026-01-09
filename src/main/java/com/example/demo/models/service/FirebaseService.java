package com.example.demo.models.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    public void enviarNotificacion(String tokenDestino, String titulo, String cuerpo) {
        if (tokenDestino == null || tokenDestino.isEmpty()) {
            System.out.println(">>> Error: Usuario sin token FCM");
            return;
        }

        Notification notification = Notification.builder()
                .setTitle(titulo)
                .setBody(cuerpo)
                .build();

        Message message = Message.builder()
                .setToken(tokenDestino)
                .setNotification(notification)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            System.out.println(">>> Notificación enviada con éxito a: " + tokenDestino);
        } catch (Exception e) {
            System.err.println("Error enviando FCM: " + e.getMessage());
        }
    }
}
