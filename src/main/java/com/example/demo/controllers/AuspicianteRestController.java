package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.entity.Auspiciante;
import com.example.demo.models.service.IAuspicianteService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuspicianteRestController {
	
	@Autowired
	private IAuspicianteService auspicianteService;

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

	@PostMapping("/auspiciantes")
	public ResponseEntity<?> create(@RequestBody Auspiciante auspiciante) {
		// 1. Validar si ya existe por nombre o código
		List<Auspiciante> todos = auspicianteService.findAll();
		
		boolean existeNombre = todos.stream()
				.anyMatch(a -> a.getNombre().equalsIgnoreCase(auspiciante.getNombre()));
		
		if (existeNombre) {
			return ResponseEntity.badRequest()
					.body(Map.of("mensaje", "Ya existe un auspiciante con ese nombre."));
		}

		if (auspiciante.getCodigo() != null) {
			boolean existeCodigo = todos.stream()
					.anyMatch(a -> a.getCodigo() != null && a.getCodigo().equalsIgnoreCase(auspiciante.getCodigo()));
			
			if (existeCodigo) {
				return ResponseEntity.badRequest()
						.body(Map.of("mensaje", "Ya existe un auspiciante con ese código."));
			}
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(auspicianteService.save(auspiciante));
	}

	@PutMapping("/auspiciantes/{id}")
	public ResponseEntity<?> update(@RequestBody Auspiciante auspiciante, @PathVariable Long id) {
		Auspiciante auspicianteActual = auspicianteService.findById(id);

		if (auspicianteActual == null) {
			return ResponseEntity.notFound().build();
		}

		// 1. Validar duplicados al editar (excluyendo al actual)
		List<Auspiciante> todos = auspicianteService.findAll();

		boolean nombreDuplicado = todos.stream()
				.anyMatch(a -> !a.getId_auspiciante().equals(id) && a.getNombre().equalsIgnoreCase(auspiciante.getNombre()));

		if (nombreDuplicado) {
			return ResponseEntity.badRequest()
					.body(Map.of("mensaje", "Ya existe otro auspiciante con ese nombre."));
		}

		if (auspiciante.getCodigo() != null) {
			boolean codigoDuplicado = todos.stream()
					.anyMatch(a -> !a.getId_auspiciante().equals(id) && 
								   a.getCodigo() != null && 
								   a.getCodigo().equalsIgnoreCase(auspiciante.getCodigo()));
			if (codigoDuplicado) {
				return ResponseEntity.badRequest()
						.body(Map.of("mensaje", "Ya existe otro auspiciante con ese código."));
			}
		}

		auspicianteActual.setNombre(auspiciante.getNombre());
		auspicianteActual.setDescripcion(auspiciante.getDescripcion());
		auspicianteActual.setImagen(auspiciante.getImagen());
		auspicianteActual.setCodigo(auspiciante.getCodigo());

		return ResponseEntity.status(HttpStatus.CREATED).body(auspicianteService.save(auspicianteActual));
	}

	@DeleteMapping("/auspiciantes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		auspicianteService.delete(id);
	}
}