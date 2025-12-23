package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.entity.Recompensa;
import com.example.demo.models.service.IRecompensaService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class RecompensaRestController {
	
	@Autowired
	private IRecompensaService recompensaService;

	@GetMapping("/recompensas")
	public List<Recompensa> index() {
		return recompensaService.findAll();
	}

	@GetMapping("/recompensas/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Recompensa recompensa = recompensaService.findById(id);
		if (recompensa == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(recompensa);
	}

	@PostMapping("/recompensas")
	@ResponseStatus(HttpStatus.CREATED)
	public Recompensa create(@RequestBody Recompensa recompensa) {
		return recompensaService.save(recompensa);
	}

	@PutMapping("/recompensas/{id}")
	public ResponseEntity<?> update(@RequestBody Recompensa recompensa, @PathVariable Long id) {
		Recompensa recompensaActual = recompensaService.findById(id);

		if (recompensaActual == null) {
			return ResponseEntity.notFound().build();
		}

		recompensaActual.setNombre(recompensa.getNombre());
		recompensaActual.setDescripcion(recompensa.getDescripcion());
		recompensaActual.setCostoPuntos(recompensa.getCostoPuntos());
		
	
		recompensaActual.setDireccion(recompensa.getDireccion());
		recompensaActual.setLatitud(recompensa.getLatitud());
		recompensaActual.setLongitud(recompensa.getLongitud());

		recompensaActual.setAuspiciante(recompensa.getAuspiciante());

		Recompensa actualizada = recompensaService.save(recompensaActual);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(actualizada);
	}

	@DeleteMapping("/recompensas/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		recompensaService.delete(id);
	}
}