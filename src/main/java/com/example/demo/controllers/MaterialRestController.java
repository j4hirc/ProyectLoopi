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

import com.example.demo.models.entity.Material;
import com.example.demo.models.service.IMaterialService;
import com.example.demo.models.service.SupabaseStorageService; // Tu servicio de nube

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MaterialRestController {
	
	@Autowired
	private IMaterialService materialService;

    // 1. Inyectamos Supabase
    @Autowired
    private SupabaseStorageService storageService;

    // 2. Herramienta para leer JSON
    private ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/materiales")
	public List<Material> indext() {
		return materialService.findAll();
	}

	@GetMapping("/materiales/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Material material = materialService.findById(id);
        if (material == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(material);
	}


	@PostMapping(value = "/materiales", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Material material;
        try {
            // Convertir texto JSON a Objeto
            material = objectMapper.readValue(datosJson, Material.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        // Subir imagen a Supabase
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            material.setImagen(urlImagen);
        }

		Material nuevo = materialService.save(material);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
	}

    // =================================================================
    // ACTUALIZAR MATERIAL
    // =================================================================
	@PutMapping(value = "/materiales/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(
            @PathVariable Long id, 
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Material materialInput;
        try {
            materialInput = objectMapper.readValue(datosJson, Material.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

		Material materialActual = materialService.findById(id);
        if (materialActual == null) {
            return ResponseEntity.notFound().build();
        }

        // 1. Si mandan foto nueva, la subimos
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            materialActual.setImagen(urlImagen);
        }

        // 2. Actualizar campos
        if (materialInput.getNombre() != null) materialActual.setNombre(materialInput.getNombre());
        if (materialInput.getTipo_Material() != null) materialActual.setTipo_Material(materialInput.getTipo_Material());
        if (materialInput.getPuntosPorKg() != null) materialActual.setPuntosPorKg(materialInput.getPuntosPorKg());
        if (materialInput.getDescripcion() != null) materialActual.setDescripcion(materialInput.getDescripcion());
        
        // La imagen ya se manejó arriba, si no hay archivo nuevo, se queda la vieja.

		Material actualizado = materialService.save(materialActual);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
	}

	@DeleteMapping("/materiales/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		materialService.delete(id);
	}
}