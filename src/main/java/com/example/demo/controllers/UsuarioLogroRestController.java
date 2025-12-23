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

import com.example.demo.models.entity.Logro;
import com.example.demo.models.entity.UsuarioLogro;
import com.example.demo.models.service.IUsuarioLogroService;

@RestController
@RequestMapping("/api")
public class UsuarioLogroRestController {
	
	@Autowired
	private IUsuarioLogroService usuarioLogroService;

	@GetMapping("/usuario_logros")
	public List<UsuarioLogro> indext() {
		return usuarioLogroService.findAll();
	}

	@GetMapping("/usuario_logros/{id}")
	public UsuarioLogro show(@PathVariable Long id) {
		return usuarioLogroService.findById(id);
	}

	@PostMapping("/usuario_logros")
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioLogro create(@RequestBody UsuarioLogro usuarioLogro) {
		return usuarioLogroService.save(usuarioLogro);
	}



	@DeleteMapping("/usuario_logros/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		usuarioLogroService.delete(id);
	}
	
	
	@GetMapping("/usuarios/{cedula}/logros")
	public List<Logro> obtenerLogrosUsuario(@PathVariable Long cedula) {

	    List<UsuarioLogro> lista = usuarioLogroService.findByUsuarioCedula(cedula);

	    return lista.stream()
	            .map(UsuarioLogro::getLogro)
	            .toList();
	}


}
