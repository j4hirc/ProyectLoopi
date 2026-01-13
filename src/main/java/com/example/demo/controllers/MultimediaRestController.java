package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; 

import com.fasterxml.jackson.databind.ObjectMapper; 

import com.example.demo.models.entity.Multimedia;
import com.example.demo.models.service.IMultimedioService;
import com.example.demo.models.service.SupabaseStorageService; 

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class MultimediaRestController {

    @Autowired
    private IMultimedioService multimedioService;


    @Autowired
    private SupabaseStorageService storageService;


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

    @PostMapping(value = "/multimedias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) 
    public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Multimedia multimedia;
        try {
            multimedia = objectMapper.readValue(datosJson, Multimedia.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            multimedia.setImagenes(urlImagen); 
        }

        Multimedia nuevo = multimedioService.save(multimedia);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

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

        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            actual.setImagenes(urlImagen);
        }

        if (multimediaInput.getTitulo() != null) actual.setTitulo(multimediaInput.getTitulo());
        if (multimediaInput.getDescripcion() != null) actual.setDescripcion(multimediaInput.getDescripcion());

        Multimedia actualizado = multimedioService.save(actual);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
    }

    @DeleteMapping("/multimedias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        multimedioService.delete(id);
    }
}