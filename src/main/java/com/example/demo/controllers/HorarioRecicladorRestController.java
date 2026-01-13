package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.entity.FormularioReciclador;
import com.example.demo.models.entity.HorarioReciclador;
import com.example.demo.models.service.IFormularioRecicladorService;
import com.example.demo.models.service.IHorarioRecicladorService;

@RestController
@RequestMapping("/api")
public class HorarioRecicladorRestController {
	
	@Autowired
	private IHorarioRecicladorService horarioRecicladorService;
	
	@Autowired
	private IFormularioRecicladorService formularioService;

	@GetMapping("/horario_recicladores")
	public List<HorarioReciclador> indext() {
		return horarioRecicladorService.findAll();
	}

	@GetMapping("/horario_recicladores/{id}")
	public HorarioReciclador show(@PathVariable Long id) {
		return horarioRecicladorService.findById(id);
	}

	@PostMapping("/horario_recicladores")
	@ResponseStatus(HttpStatus.CREATED)
	public HorarioReciclador create(
	        @RequestBody HorarioReciclador horarioReciclador, 
	        @RequestParam Long id_formulario) { 
	    
	    FormularioReciclador formulario = formularioService.findById(id_formulario);
	    
	    if (formulario != null) {
	        horarioReciclador.setFormulario(formulario);
	    }
	    
	
	    return horarioRecicladorService.save(horarioReciclador);
	}

	@PutMapping("/horario_recicladores/{id}")
	public HorarioReciclador update(@RequestBody HorarioReciclador horarioReciclador, @PathVariable Long id) {
		HorarioReciclador horarioRecicladorActual = horarioRecicladorService.findById(id);

		horarioRecicladorActual.setDia_semana(horarioReciclador.getDia_semana());
		horarioRecicladorActual.setHora_inicio(horarioReciclador.getHora_inicio());
		horarioRecicladorActual.setHora_fin(horarioReciclador.getHora_fin());



		return horarioRecicladorService.save(horarioRecicladorActual);
	}

	@DeleteMapping("/horario_recicladores/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		horarioRecicladorService.delete(id);
	}

}
