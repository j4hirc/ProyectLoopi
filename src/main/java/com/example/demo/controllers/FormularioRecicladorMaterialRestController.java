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

import com.example.demo.models.entity.FormularioRecicladorMaterial;
import com.example.demo.models.service.IFormularioRecicladorMaterialService;

@RestController
@RequestMapping("/api")
public class FormularioRecicladorMaterialRestController {
	
	@Autowired
	private IFormularioRecicladorMaterialService formularioRecicladorMaterialService;

	@GetMapping("/formulario_reciclador_materiales")
	public List<FormularioRecicladorMaterial> indext() {
		return formularioRecicladorMaterialService.findAll();
	}

	@GetMapping("/formulario_reciclador_materiales/{id}")
	public FormularioRecicladorMaterial show(@PathVariable Long id) {
		return formularioRecicladorMaterialService.findById(id);
	}

	@PostMapping("/formulario_reciclador_materiales")
	@ResponseStatus(HttpStatus.CREATED)
	public FormularioRecicladorMaterial create(@RequestBody FormularioRecicladorMaterial formularioRecicladorMaterial) {
		return formularioRecicladorMaterialService.save(formularioRecicladorMaterial);
	}



	@DeleteMapping("/formulario_reciclador_materiales/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		formularioRecicladorMaterialService.delete(id);
	}
	
	@GetMapping("/formularios_reciclador_material/formulario/{idFormulario}")
    public List<FormularioRecicladorMaterial> listarPorFormulario(
            @PathVariable Long idFormulario
    ) {
        return formularioRecicladorMaterialService.findByFormulario(idFormulario);
    }

}
