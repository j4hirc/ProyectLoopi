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


import com.example.demo.models.entity.Ciudad;
import com.example.demo.models.service.ICiudadService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CiudadRestController {

	
	@Autowired
	private ICiudadService ciudadService;

	@GetMapping("/ciudades")
	public List<Ciudad> indext() {
		return ciudadService.findAll();
	}

	@GetMapping("/ciudades/{id}")
	public Ciudad show(@PathVariable Long id) {
		return ciudadService.findById(id);
	}

	@PostMapping("/ciudades")
	@ResponseStatus(HttpStatus.CREATED)
	public Ciudad create(@RequestBody Ciudad ciudad) {
		return ciudadService.save(ciudad);
	}

	@PutMapping("/ciudades/{id}")
	public Ciudad update(@RequestBody Ciudad ciudad, @PathVariable Long id) {
		Ciudad ciudadActual = ciudadService.findById(id);

		ciudadActual.setNombre_ciudad(ciudad.getNombre_ciudad());


		return ciudadService.save(ciudadActual);
	}

	@DeleteMapping("/ciudades/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		ciudadService.delete(id);
	}
}
