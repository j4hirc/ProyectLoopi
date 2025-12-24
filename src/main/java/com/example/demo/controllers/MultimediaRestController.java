package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Importante para archivos

import com.fasterxml.jackson.databind.ObjectMapper; // Importante para JSON

import com.example.demo.models.entity.Multimedia;
import com.example.demo.models.service.IMultimedioService;
import com.example.demo.models.service.SupabaseStorageService; // Tu servicio de nube

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class MultimediaRestController {

    @Autowired
    private IMultimedioService multimedioService;

    // 1. Inyectamos el servicio de almacenamiento
    @Autowired
    private SupabaseStorageService storageService;

    // 2. Herramienta para leer JSON desde texto
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/multimedias")
    public List<Multimedia> index() {
        return multimedioService.findAll();
    }

    @GetMapping("/multimedias/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Multimedia multimedia = multimedioService.findById(id);
        if (multimedia == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(multimedia);
    }

    // =================================================================
    // CREAR MULTIMEDIA (Con foto obligatoria u opcional según decidas)
    // =================================================================
    @PostMapping(value = "/multimedias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) 
    public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Multimedia multimedia;
        try {
            // Convertimos el String JSON a Objeto Java
            multimedia = objectMapper.readValue(datosJson, Multimedia.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        // Subir imagen a Supabase
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            multimedia.setImagenes(urlImagen); // Guardamos la URL en la BD
        }

        Multimedia nuevo = multimedioService.save(multimedia);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // =================================================================
    // ACTUALIZAR MULTIMEDIA
    // =================================================================
    @PutMapping(value = "/multimedias/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id, 
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Multimedia multimediaInput;
        try {
            multimediaInput = objectMapper.readValue(datosJson, Multimedia.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        Multimedia actual = multimedioService.findById(id);
        if (actual == null) {
            return ResponseEntity.notFound().build();
        }

        // 1. Si mandan foto nueva, la subimos y reemplazamos el link
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            actual.setImagenes(urlImagen);
        }

        // 2. Actualizamos texto
        if (multimediaInput.getTitulo() != null) actual.setTitulo(multimediaInput.getTitulo());
        if (multimediaInput.getDescripcion() != null) actual.setDescripcion(multimediaInput.getDescripcion());

        // Nota: Si no mandan foto nueva, 'actual.setImagenes' no se toca, manteniendo la vieja.

        Multimedia actualizado = multimedioService.save(actual);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
    }

    @DeleteMapping("/multimedias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        multimedioService.delete(id);
    }
}