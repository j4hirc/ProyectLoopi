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

import com.example.demo.models.entity.Logro;
import com.example.demo.models.service.ILogroService;
import com.example.demo.models.service.SupabaseStorageService; // Tu servicio de nube

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LogroRestController {

	@Autowired
	private ILogroService logroService;

    // 1. Inyectamos el servicio de almacenamiento
    @Autowired
    private SupabaseStorageService storageService;

    // 2. Herramienta para leer JSON
    private ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/logros")
	public List<Logro> indext() {
		return logroService.findAll();
	}

	@GetMapping("/logros/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Logro logro = logroService.findById(id);
        if (logro == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(logro);
	}

    // =================================================================
    // CREAR LOGRO (Con imagen opcional)
    // =================================================================
	@PostMapping(value = "/logros", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Logro logro;
        try {
            // Convertir texto JSON a Objeto
            logro = objectMapper.readValue(datosJson, Logro.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        // Subir imagen a Supabase
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            logro.setImagen_logro(urlImagen);
        }

		Logro nuevo = logroService.save(logro);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
	}

    // =================================================================
    // ACTUALIZAR LOGRO
    // =================================================================
	@PutMapping(value = "/logros/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(
            @PathVariable Long id, 
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Logro logroInput;
        try {
            logroInput = objectMapper.readValue(datosJson, Logro.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

		Logro logroActual = logroService.findById(id);
        if (logroActual == null) {
            return ResponseEntity.notFound().build();
        }

        // 1. Si mandan nueva imagen, la subimos
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            logroActual.setImagen_logro(urlImagen);
        }

        // 2. Actualizar campos
        if (logroInput.getNombre() != null) logroActual.setNombre(logroInput.getNombre());
        if (logroInput.getDescripcion() != null) logroActual.setDescripcion(logroInput.getDescripcion());
        if (logroInput.getPuntos_ganados() != null) logroActual.setPuntos_ganados(logroInput.getPuntos_ganados());
        
        // No tocamos la imagen si no enviaron archivo nuevo (se mantiene la vieja)

		Logro actualizado = logroService.save(logroActual);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
	}

	@DeleteMapping("/logros/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		logroService.delete(id);
	}
}