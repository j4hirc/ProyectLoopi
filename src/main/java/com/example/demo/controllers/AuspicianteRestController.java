package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Importante

import com.fasterxml.jackson.databind.ObjectMapper; // Importante

import com.example.demo.models.entity.Auspiciante;
import com.example.demo.models.service.IAuspicianteService;
import com.example.demo.models.service.SupabaseStorageService; // Tu servicio de Nube

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuspicianteRestController {
	
	@Autowired
	private IAuspicianteService auspicianteService;

    @Autowired
    private SupabaseStorageService storageService;

    private ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/auspiciantes")
	public List<Auspiciante> index() {
		return auspicianteService.findAll();
	}

	@GetMapping("/auspiciantes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Auspiciante auspiciante = auspicianteService.findById(id);
		if (auspiciante == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(auspiciante);
	}


	@PostMapping(value = "/auspiciantes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Auspiciante auspiciante;
        try {
            auspiciante = objectMapper.readValue(datosJson, Auspiciante.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

		List<Auspiciante> todos = auspicianteService.findAll();
		
		boolean existeNombre = todos.stream()
				.anyMatch(a -> a.getNombre().equalsIgnoreCase(auspiciante.getNombre()));
		
		if (existeNombre) {
			return ResponseEntity.badRequest().body(Map.of("mensaje", "Ya existe un auspiciante con ese nombre."));
		}

		if (auspiciante.getCodigo() != null) {
			boolean existeCodigo = todos.stream()
					.anyMatch(a -> a.getCodigo() != null && a.getCodigo().equalsIgnoreCase(auspiciante.getCodigo()));
			
			if (existeCodigo) {
				return ResponseEntity.badRequest().body(Map.of("mensaje", "Ya existe un auspiciante con ese código."));
			}
		}

        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            auspiciante.setImagen(urlImagen);
        }

		return ResponseEntity.status(HttpStatus.CREATED).body(auspicianteService.save(auspiciante));
	}


	@PutMapping(value = "/auspiciantes/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Auspiciante auspicianteInput;
        try {
            auspicianteInput = objectMapper.readValue(datosJson, Auspiciante.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

		Auspiciante auspicianteActual = auspicianteService.findById(id);
		if (auspicianteActual == null) {
			return ResponseEntity.notFound().build();
		}

		List<Auspiciante> todos = auspicianteService.findAll();

		boolean nombreDuplicado = todos.stream()
				.anyMatch(a -> !a.getId_auspiciante().equals(id) && a.getNombre().equalsIgnoreCase(auspicianteInput.getNombre()));

		if (nombreDuplicado) {
			return ResponseEntity.badRequest().body(Map.of("mensaje", "Ya existe otro auspiciante con ese nombre."));
		}

		if (auspicianteInput.getCodigo() != null) {
			boolean codigoDuplicado = todos.stream()
					.anyMatch(a -> !a.getId_auspiciante().equals(id) && 
								   a.getCodigo() != null && 
								   a.getCodigo().equalsIgnoreCase(auspicianteInput.getCodigo()));
			if (codigoDuplicado) {
				return ResponseEntity.badRequest().body(Map.of("mensaje", "Ya existe otro auspiciante con ese código."));
			}
		}

        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            auspicianteActual.setImagen(urlImagen);
        }

		auspicianteActual.setNombre(auspicianteInput.getNombre());
		auspicianteActual.setDescripcion(auspicianteInput.getDescripcion());
		auspicianteActual.setCodigo(auspicianteInput.getCodigo());
        

		return ResponseEntity.status(HttpStatus.CREATED).body(auspicianteService.save(auspicianteActual));
	}

	@DeleteMapping("/auspiciantes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		auspicianteService.delete(id);
	}
}