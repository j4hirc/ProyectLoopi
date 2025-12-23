package com.example.demo.controllers;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.DAO.INotificacionDao;
import com.example.demo.models.entity.FormularioReciclador;
import com.example.demo.models.entity.Notificacion;
import com.example.demo.models.service.IFormularioRecicladorService;
import com.example.demo.models.service.INotificacionService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class FormularioRecicladorRestController {
	
	@Autowired
	private IFormularioRecicladorService formularioRecicladorService;

	@Autowired
	private INotificacionService notificacionService;
	
	@GetMapping("/formularios_reciclador") 
	public List<FormularioReciclador> index() {
		return formularioRecicladorService.findAll();
	}

	@GetMapping("/formularios_reciclador/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		FormularioReciclador form = formularioRecicladorService.findById(id);
		if(form == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(form);
	}

	@PostMapping("/formularios_reciclador")
	@ResponseStatus(HttpStatus.CREATED)
	public FormularioReciclador create(@RequestBody FormularioReciclador formularioReciclador) {
		return formularioRecicladorService.save(formularioReciclador);
	}

	@PutMapping("/formularios_reciclador/{id}")
	public ResponseEntity<?> update(
	        @RequestBody FormularioReciclador formularioReciclador,
	        @PathVariable Long id) {

		FormularioReciclador formularioActual = formularioRecicladorService.findById(id);

	    if (formularioActual == null)
	        return ResponseEntity.notFound().build();

	    Boolean aprobadoAntes = formularioActual.getAprobado();

	    formularioActual.setAnios_experiencia(formularioReciclador.getAnios_experiencia());
	    formularioActual.setNombre_sitio(formularioReciclador.getNombre_sitio());
	    formularioActual.setLatitud(formularioReciclador.getLatitud());
	    formularioActual.setLongitud(formularioReciclador.getLongitud());
	    formularioActual.setUbicacion(formularioReciclador.getUbicacion());
	    formularioActual.setFoto_perfil_profesional(formularioReciclador.getFoto_perfil_profesional());
	    formularioActual.setEvidencia_experiencia(formularioReciclador.getEvidencia_experiencia());
	    formularioActual.setAprobado(formularioReciclador.getAprobado());
	    formularioActual.setObservacion_admin(formularioReciclador.getObservacion_admin());

	    if (formularioReciclador.getHorarios() != null) {
	        formularioActual.getHorarios().clear();
	        formularioActual.getHorarios().addAll(formularioReciclador.getHorarios());
	    }
	    
	    if (formularioReciclador.getMateriales() != null) {
	        formularioActual.getMateriales().clear();
	        formularioActual.getMateriales().addAll(formularioReciclador.getMateriales());
	    }

	    FormularioReciclador actualizado = formularioRecicladorService.save(formularioActual);

	 
	    if (aprobadoAntes == null && actualizado.getAprobado() != null) {

	        Notificacion notificacion = new Notificacion();
	        notificacion.setUsuario(actualizado.getUsuario());
	        notificacion.setFecha_creacion(LocalDateTime.now());
	        notificacion.setLeido(false);
	        notificacion.setTipo("FORMULARIO");
	        notificacion.setEntidad_referencia("FORMULARIO");
	        notificacion.setId_referencia(actualizado.getId_formulario());

	        if (Boolean.TRUE.equals(actualizado.getAprobado())) {
	            notificacion.setTitulo("Solicitud aprobada");
	            notificacion.setMensaje(
	                "Tu solicitud para ser reciclador fue aprobada. ¡Bienvenido a Loopi!"
	            );
	        } else {
	            notificacion.setTitulo("Solicitud rechazada");
	            notificacion.setMensaje(
	                actualizado.getObservacion_admin() != null
	                    ? actualizado.getObservacion_admin()
	                    : "Tu solicitud fue rechazada por el administrador."
	            );
	        }

	        notificacionService.save(notificacion);
	    }

	    return ResponseEntity.ok(actualizado);
	}


	

    @GetMapping("/formularios_reciclador/usuario/{idUsuario}")
    public ResponseEntity<?> buscarPorUsuario(@PathVariable Long idUsuario) {
        
        FormularioReciclador encontrado = formularioRecicladorService.findAll().stream()
            .filter(f -> f.getUsuario() != null && f.getUsuario().getCedula().equals(idUsuario))
            .findFirst()
            .orElse(null);

        if (encontrado != null) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("existe", true);
            respuesta.put("id_formulario", encontrado.getId_formulario());
            
            // CORRECCIÓN AQUÍ: Usar getAprobado() en lugar de is()
            respuesta.put("aprobado", encontrado.getAprobado()); 
            respuesta.put("observacion", encontrado.getObservacion_admin());
            
            return ResponseEntity.ok(respuesta); // Retorna 200 OK con el JSON simple
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found (Usuario libre para registrarse)
        }
    }
    
    @PutMapping("/formularios_reciclador/aprobar/{id}")
    public ResponseEntity<?> aprobar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String observacion = body.get("observacion_admin");
        formularioRecicladorService.aprobarFormulario(id, observacion);
        return ResponseEntity.ok().body(Map.of("mensaje", "Formulario aprobado con éxito"));
    }

    
}