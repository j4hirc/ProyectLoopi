package com.example.demo.controllers;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Importante

import com.fasterxml.jackson.databind.ObjectMapper; // Importante

import com.example.demo.models.entity.FormularioReciclador;
import com.example.demo.models.entity.Notificacion;
import com.example.demo.models.service.IFormularioRecicladorService;
import com.example.demo.models.service.INotificacionService;
import com.example.demo.models.service.SupabaseStorageService; // Tu servicio de Nube

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class FormularioRecicladorRestController {
	
	@Autowired
	private IFormularioRecicladorService formularioRecicladorService;

	@Autowired
	private INotificacionService notificacionService;

    // 1. Inyectamos Supabase
    @Autowired
    private SupabaseStorageService storageService;

    private ObjectMapper objectMapper = new ObjectMapper();
	
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

    // =================================================================
    // CREAR FORMULARIO (Con 2 fotos opcionales)
    // =================================================================
	@PostMapping(value = "/formularios_reciclador", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson,
            @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil,
            @RequestParam(value = "evidencia", required = false) MultipartFile evidencia
    ) {
        FormularioReciclador formularioReciclador;
        try {
            formularioReciclador = objectMapper.readValue(datosJson, FormularioReciclador.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        // Subir Foto Perfil Profesional
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            String urlFoto = storageService.subirImagen(fotoPerfil);
            formularioReciclador.setFoto_perfil_profesional(urlFoto);
        }

        // Subir Evidencia Experiencia
        if (evidencia != null && !evidencia.isEmpty()) {
            String urlEvidencia = storageService.subirImagen(evidencia);
            formularioReciclador.setEvidencia_experiencia(urlEvidencia);
        }

		FormularioReciclador nuevo = formularioRecicladorService.save(formularioReciclador);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
	}

    // =================================================================
    // ACTUALIZAR FORMULARIO (Con 2 fotos opcionales)
    // =================================================================
	@PutMapping(value = "/formularios_reciclador/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam("datos") String datosJson,
            @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil,
            @RequestParam(value = "evidencia", required = false) MultipartFile evidencia
    ) {
        FormularioReciclador formularioInput;
        try {
            formularioInput = objectMapper.readValue(datosJson, FormularioReciclador.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

		FormularioReciclador formularioActual = formularioRecicladorService.findById(id);
	    if (formularioActual == null)
	        return ResponseEntity.notFound().build();

	    Boolean aprobadoAntes = formularioActual.getAprobado();

        // 1. Subir Foto Perfil si la envían nueva
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            String urlFoto = storageService.subirImagen(fotoPerfil);
            formularioActual.setFoto_perfil_profesional(urlFoto);
        }

        // 2. Subir Evidencia si la envían nueva
        if (evidencia != null && !evidencia.isEmpty()) {
            String urlEvidencia = storageService.subirImagen(evidencia);
            formularioActual.setEvidencia_experiencia(urlEvidencia);
        }

        // 3. Actualizar datos
	    formularioActual.setAnios_experiencia(formularioInput.getAnios_experiencia());
	    formularioActual.setNombre_sitio(formularioInput.getNombre_sitio());
	    formularioActual.setLatitud(formularioInput.getLatitud());
	    formularioActual.setLongitud(formularioInput.getLongitud());
	    formularioActual.setUbicacion(formularioInput.getUbicacion());
	    
        // Ojo: Si no mandan archivo, no tocamos las fotos viejas (se mantienen las de la BD)
        
	    formularioActual.setAprobado(formularioInput.getAprobado());
	    formularioActual.setObservacion_admin(formularioInput.getObservacion_admin());

	    if (formularioInput.getHorarios() != null) {
	        formularioActual.getHorarios().clear();
	        formularioActual.getHorarios().addAll(formularioInput.getHorarios());
	    }
	    
	    if (formularioInput.getMateriales() != null) {
	        formularioActual.getMateriales().clear();
	        formularioActual.getMateriales().addAll(formularioInput.getMateriales());
	    }

	    FormularioReciclador actualizado = formularioRecicladorService.save(formularioActual);

        // 4. Lógica de Notificaciones (Intacta)
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
	            notificacion.setMensaje("Tu solicitud para ser reciclador fue aprobada. ¡Bienvenido a Loopi!");
	        } else {
	            notificacion.setTitulo("Solicitud rechazada");
	            notificacion.setMensaje(actualizado.getObservacion_admin() != null ? actualizado.getObservacion_admin() : "Tu solicitud fue rechazada por el administrador.");
	        }
	        notificacionService.save(notificacion);
	    }

	    return ResponseEntity.ok(actualizado);
	}

    // =================================================================
    // MÉTODOS AUXILIARES (Sin cambios)
    // =================================================================

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
            
            respuesta.put("aprobado", encontrado.getAprobado()); 
            respuesta.put("observacion", encontrado.getObservacion_admin());
            
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.notFound().build(); 
        }
    }
    
    @PutMapping("/formularios_reciclador/aprobar/{id}")
    public ResponseEntity<?> aprobar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String observacion = body.get("observacion_admin");
        formularioRecicladorService.aprobarFormulario(id, observacion);
        return ResponseEntity.ok().body(Map.of("mensaje", "Formulario aprobado con éxito"));
    }
}