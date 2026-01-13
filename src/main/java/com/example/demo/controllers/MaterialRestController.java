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

import com.example.demo.models.entity.Material;
import com.example.demo.models.service.IMaterialService;
import com.example.demo.models.service.SupabaseStorageService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MaterialRestController {
	
	@Autowired
	private IMaterialService materialService;

    @Autowired
    private SupabaseStorageService storageService;

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
            material = objectMapper.readValue(datosJson, Material.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            material.setImagen(urlImagen);
        }

		Material nuevo = materialService.save(material);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
	}


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

        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            materialActual.setImagen(urlImagen);
        }

        if (materialInput.getNombre() != null) materialActual.setNombre(materialInput.getNombre());
        if (materialInput.getTipo_Material() != null) materialActual.setTipo_Material(materialInput.getTipo_Material());
        if (materialInput.getPuntosPorKg() != null) materialActual.setPuntosPorKg(materialInput.getPuntosPorKg());
        if (materialInput.getDescripcion() != null) materialActual.setDescripcion(materialInput.getDescripcion());

		Material actualizado = materialService.save(materialActual);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
	}

	@DeleteMapping("/materiales/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		materialService.delete(id);
	}
}