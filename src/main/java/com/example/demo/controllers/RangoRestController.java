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

import com.example.demo.models.entity.Rango;
import com.example.demo.models.service.IRangoService;
import com.example.demo.models.service.SupabaseStorageService; // Tu servicio de nube

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RangoRestController {
	
	@Autowired
	private IRangoService rangoService;

    // 1. Inyectamos el servicio de almacenamiento
    @Autowired
    private SupabaseStorageService storageService;

    // 2. Herramienta para leer JSON
    private ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/rangos")
	public List<Rango> indext() {
		return rangoService.findAll();
	}

	@GetMapping("/rangos/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Rango rango = rangoService.findById(id);
        if (rango == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rango);
	}

    // =================================================================
    // CREAR RANGO (Con ícono opcional/obligatorio)
    // =================================================================
	@PostMapping(value = "/rangos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Rango rango;
        try {
            // Convertir texto JSON a Objeto
            rango = objectMapper.readValue(datosJson, Rango.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        // Subir imagen a Supabase
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            rango.setImagen(urlImagen);
        }

		Rango nuevo = rangoService.save(rango);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
	}

    // =================================================================
    // ACTUALIZAR RANGO
    // =================================================================
	@PutMapping(value = "/rangos/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(
            @PathVariable Long id, 
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Rango rangoInput;
        try {
            rangoInput = objectMapper.readValue(datosJson, Rango.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

		Rango rangoActual = rangoService.findById(id);
        if (rangoActual == null) {
            return ResponseEntity.notFound().build();
        }

        // 1. Si mandan nueva imagen, la subimos
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            rangoActual.setImagen(urlImagen);
        }

        // 2. Actualizar nombre y otros datos
		if (rangoInput.getNombre_rango() != null) {
            rangoActual.setNombre_rango(rangoInput.getNombre_rango());
        }
        
        // Nota: No tocamos la imagen si no enviaron archivo nuevo, 
        // para no borrar la que ya estaba.

		Rango actualizado = rangoService.save(rangoActual);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
	}

	@DeleteMapping("/rangos/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		rangoService.delete(id);
	}
}