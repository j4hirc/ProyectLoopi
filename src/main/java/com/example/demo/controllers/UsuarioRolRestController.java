package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.entity.UsuarioRol;
import com.example.demo.models.service.IUsuarioRolService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UsuarioRolRestController {
	

	@Autowired
	private IUsuarioRolService usuarioRolService;

	@GetMapping("/usuario_rol")
	public List<UsuarioRol> indext() {
		return usuarioRolService.findAll();
	}

	@GetMapping("/usuario_rol/{id}")
	public UsuarioRol show(@PathVariable Long id) {
		return usuarioRolService.findById(id);
	}

	@PostMapping("/usuario_rol")
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioRol create(@RequestBody UsuarioRol UsuarioRol) {
		return usuarioRolService.save(UsuarioRol);
	}



	@DeleteMapping("/usuario_rol/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		usuarioRolService.delete(id);
	}
	
	@GetMapping("/usuario_rol/usuario/{usuarioId}")
	public List<UsuarioRol> getRolesPorUsuario(@PathVariable Long usuarioId) {
	    return usuarioRolService.findByUsuarioId(usuarioId);
	}
	

}
