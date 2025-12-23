package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import com.example.demo.models.entity.Rango;
import com.example.demo.models.service.IRangoService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RangoRestController {
	
	@Autowired
	private IRangoService rangoService;

	@GetMapping("/rangos")
	public List<Rango> indext() {
		return rangoService.findAll();
	}

	@GetMapping("/rangos/{id}")
	public Rango show(@PathVariable Long id) {
		return rangoService.findById(id);
	}

	@PostMapping("/rangos")
	@ResponseStatus(HttpStatus.CREATED)
	public Rango create(@RequestBody Rango rango) {
		return rangoService.save(rango);
	}

	@PutMapping("/rangos/{id}")
	public Rango update(@RequestBody Rango rango, @PathVariable Long id) {
		Rango RangoActual = rangoService.findById(id);

		RangoActual.setNombre_rango(rango.getNombre_rango());
		RangoActual.setImagen(rango.getImagen());



		return rangoService.save(RangoActual);
	}

	@DeleteMapping("/rangos/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		rangoService.delete(id);
	}

}
