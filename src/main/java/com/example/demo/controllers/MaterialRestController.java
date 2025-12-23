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


import com.example.demo.models.entity.Material;
import com.example.demo.models.service.IMaterialService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MaterialRestController {
	
	@Autowired
	private IMaterialService materialService;

	@GetMapping("/materiales")
	public List<Material> indext() {
		return materialService.findAll();
	}

	@GetMapping("/materiales/{id}")
	public Material show(@PathVariable Long id) {
		return materialService.findById(id);
	}

	@PostMapping("/materiales")
	@ResponseStatus(HttpStatus.CREATED)
	public Material create(@RequestBody Material material) {
		return materialService.save(material);
	}

	@PutMapping("/materiales/{id}")
	public Material update(@RequestBody Material material, @PathVariable Long id) {
		Material materialActual = materialService.findById(id);

		materialActual.setNombre(material.getNombre());
		materialActual.setTipo_Material(material.getTipo_Material());
		materialActual.setPuntosPorKg(material.getPuntosPorKg());
		materialActual.setDescripcion(material.getDescripcion());
		materialActual.setImagen(material.getImagen());



		return materialService.save(materialActual);
	}

	@DeleteMapping("/materiales/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		materialService.delete(id);
	}

}
