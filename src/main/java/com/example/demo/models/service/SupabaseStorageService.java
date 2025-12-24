package com.example.demo.models.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;

import java.util.UUID;

@Service
public class SupabaseStorageService {

    // --- TUS CREDENCIALES ---
    private final String PROJECT_URL = "https://mkrvevdwqkwyctqxddpu.supabase.co"; 
    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1rcnZldmR3cWt3eWN0cXhkZHB1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjY1OTExMzAsImV4cCI6MjA4MjE2NzEzMH0.vAUs-9G_8Mh80E_v-x2k8tEv-rbadPM_zvQIqHNtmFY"; 
    private final String BUCKET_NAME = "imagenes";

    private final RestTemplate restTemplate;

    public SupabaseStorageService() {
        this.restTemplate = new RestTemplate();
    }

    public String subirImagen(MultipartFile file) {
        try {
            // 1. Generar nombre único
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String urlApi = PROJECT_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName;

            // 2. Preparar Headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 3. Preparar el cuerpo (El archivo)
            // Truco: Usamos ByteArrayResource sobreescribiendo getFilename para que Supabase lo reconozca
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 4. Enviar Petición (POST)
            restTemplate.postForEntity(urlApi, requestEntity, String.class);

            // 5. Retornar URL Pública
            return PROJECT_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;

        } catch (Exception e) {
            System.err.println("Error subiendo a Supabase: " + e.getMessage());
            e.printStackTrace(); // Para ver el error real en consola
            return null; 
        }
    }
}