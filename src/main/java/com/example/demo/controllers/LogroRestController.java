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


import com.example.demo.models.entity.Logro;
import com.example.demo.models.service.ILogroService;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LogroRestController {

	
	@Autowired
	private ILogroService logroService;


	@GetMapping("/logros")
	public List<Logro> indext() {
		return logroService.findAll();
	}

	@GetMapping("/logros/{id}")
	public Logro show(@PathVariable Long id) {
		return logroService.findById(id);
	}

	@PostMapping("/logros")
	@ResponseStatus(HttpStatus.CREATED)
	public Logro create(@RequestBody Logro logro) {
		return logroService.save(logro);
	}

	@PutMapping("/logros/{id}")
	public Logro update(@RequestBody Logro logro, @PathVariable Long id) {
		Logro LogroActual = logroService.findById(id);

		LogroActual.setNombre(logro.getNombre());
		LogroActual.setDescripcion(logro.getDescripcion());
		LogroActual.setImagen_logro(logro.getImagen_logro());
		LogroActual.setPuntos_ganados(logro.getPuntos_ganados());

		return logroService.save(LogroActual);
	}

	@DeleteMapping("/logros/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		logroService.delete(id);
	}
	
	


}
