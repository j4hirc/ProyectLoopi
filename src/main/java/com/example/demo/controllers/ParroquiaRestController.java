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

import com.example.demo.models.entity.Parroquia;
import com.example.demo.models.service.IParroquiService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ParroquiaRestController {
	
	@Autowired
	private IParroquiService parroquiService;

	@GetMapping("/parroquias")
	public List<Parroquia> indext() {
		return parroquiService.findAll();
	}

	@GetMapping("/parroquias/{id}")
	public Parroquia show(@PathVariable Long id) {
		return parroquiService.findById(id);
	}

	@PostMapping("/parroquias")
	@ResponseStatus(HttpStatus.CREATED)
	public Parroquia create(@RequestBody Parroquia parroquia) {
		return parroquiService.save(parroquia);
	}

	@PutMapping("/parroquias/{id}")
	public Parroquia update(@RequestBody Parroquia parroquia, @PathVariable Long id) {
	    Parroquia parroquiaActual = parroquiService.findById(id);

	    if (parroquiaActual != null) {
	        parroquiaActual.setNombre_parroquia(parroquia.getNombre_parroquia());

	        parroquiaActual.setCiudad(parroquia.getCiudad());
	        
	        return parroquiService.save(parroquiaActual);
	    }
	    return null; 
	}

	@DeleteMapping("/parroquias/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		parroquiService.delete(id);
	}

}
