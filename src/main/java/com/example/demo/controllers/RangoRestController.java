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

import com.example.demo.models.entity.Rango;
import com.example.demo.models.service.IRangoService;
import com.example.demo.models.service.SupabaseStorageService; 
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RangoRestController {
	
	@Autowired
	private IRangoService rangoService;

    @Autowired
    private SupabaseStorageService storageService;

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

	@PostMapping(value = "/rangos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Rango rango;
        try {
            rango = objectMapper.readValue(datosJson, Rango.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            rango.setImagen(urlImagen);
        }

		Rango nuevo = rangoService.save(rango);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
	}

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

        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            rangoActual.setImagen(urlImagen);
        }

		if (rangoInput.getNombre_rango() != null) {
            rangoActual.setNombre_rango(rangoInput.getNombre_rango());
        }
        
		Rango actualizado = rangoService.save(rangoActual);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
	}

	@DeleteMapping("/rangos/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		rangoService.delete(id);
	}
}