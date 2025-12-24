package com.example.demo.models.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.UUID;

@Service
public class SupabaseStorageService {

    // --- REEMPLAZA ESTOS DATOS CON LOS TUYOS DE SUPABASE ---
    private final String PROJECT_URL = "https://mkrvevdwqkwyctqxddpu.supabase.co"; 
    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1rcnZldmR3cWt3eWN0cXhkZHB1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjY1OTExMzAsImV4cCI6MjA4MjE2NzEzMH0.vAUs-9G_8Mh80E_v-x2k8tEv-rbadPM_zvQIqHNtmFY"; 
    private final String BUCKET_NAME = "imagenes"; // El nombre que le pusiste al bucket

    private final WebClient webClient;

    public SupabaseStorageService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(PROJECT_URL).build();
    }

    public String subirImagen(MultipartFile file) {
        try {
            // 1. Generamos nombre único (ej: a1b2c3_foto.jpg)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 2. Preparamos el cuerpo del envío
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", file.getResource());

            // 3. Enviamos a Supabase Storage
            webClient.post()
                    .uri("/storage/v1/object/" + BUCKET_NAME + "/" + fileName)
                    .header("Authorization", "Bearer " + API_KEY)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(String.class) // Esperamos respuesta
                    .block(); // Bloqueamos para esperar que suba

            // 4. Retornamos la URL pública (para guardarla en la BD)
            return PROJECT_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;

        } catch (Exception e) {
            System.err.println("Error subiendo a Supabase: " + e.getMessage());
            return null; // O lanza una excepción si prefieres
        }
    }
}