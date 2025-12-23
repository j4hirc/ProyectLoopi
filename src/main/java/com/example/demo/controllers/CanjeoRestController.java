package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import com.example.demo.models.entity.Canjeo;
import com.example.demo.models.service.ICanjeoService;

@RestController
@RequestMapping("/api")
public class CanjeoRestController {
	

	@Autowired
	private ICanjeoService canjeoService;

	@GetMapping("/canjeos")
	public List<Canjeo> indext() {
		return canjeoService.findAll();
	}

	@GetMapping("/canjeos/{id}")
	public Canjeo show(@PathVariable Long id) {
		return canjeoService.findById(id);
	}

	@PostMapping("/canjeos")
	@ResponseStatus(HttpStatus.CREATED)
	public Canjeo create(@RequestBody Canjeo canjeo) {
		return canjeoService.save(canjeo);
	}



	@DeleteMapping("/canjeos/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		canjeoService.delete(id);
	}

}
