package com.example.demo.controllers;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // ¡IMPORTANTE PARA LocalTime!

import com.example.demo.models.entity.FormularioReciclador;
import com.example.demo.models.entity.FormularioRecicladorMaterial; // Tu entidad intermedia
import com.example.demo.models.entity.HorarioReciclador; // Tu entidad de horarios
import com.example.demo.models.entity.Notificacion;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.service.IFormularioRecicladorService;
import com.example.demo.models.service.INotificacionService;
import com.example.demo.models.service.IUsuarioService;
import com.example.demo.models.service.SupabaseStorageService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class FormularioRecicladorRestController {
    
    @Autowired
    private IFormularioRecicladorService formularioRecicladorService;

    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private SupabaseStorageService storageService;

    // 1. CONFIGURACIÓN DEL MAPPER (Soluciona error "java.time.LocalTime")
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    
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
    // CREAR FORMULARIO (CORREGIDO: FOTOS + FECHAS + VINCULACIÓN)
    // =================================================================
    @PostMapping(value = "/formularios_reciclador", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson,
            @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil,
            @RequestParam(value = "evidencia", required = false) MultipartFile evidencia
    ) {
        FormularioReciclador formularioReciclador;
        try {
            // 1. Convertir JSON a Objeto (JavaTimeModule se encarga del LocalTime)
            formularioReciclador = objectMapper.readValue(datosJson, FormularioReciclador.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        try {
            // 2. Subir Fotos a Supabase
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                String urlFoto = storageService.subirImagen(fotoPerfil);
                formularioReciclador.setFoto_perfil_profesional(urlFoto);
            }
            if (evidencia != null && !evidencia.isEmpty()) {
                String urlEvidencia = storageService.subirImagen(evidencia);
                formularioReciclador.setEvidencia_experiencia(urlEvidencia);
            }

            // 3. Validar Usuario
            if (formularioReciclador.getUsuario() != null) {
                Usuario u = usuarioService.findById(formularioReciclador.getUsuario().getCedula());
                if (u != null) formularioReciclador.setUsuario(u);
            }

            // 4. VINCULAR HORARIOS (Evita Error 500 "id_formulario cannot be null")
            if (formularioReciclador.getHorarios() != null) {
                for (HorarioReciclador h : formularioReciclador.getHorarios()) {
                    h.setFormulario(formularioReciclador); // El hijo conoce al padre
                }
            }

            // 5. VINCULAR MATERIALES (Evita Error 500)
            if (formularioReciclador.getMateriales() != null) {
                for (FormularioRecicladorMaterial m : formularioReciclador.getMateriales()) {
                    m.setFormulario(formularioReciclador); // El hijo conoce al padre
                }
            }

            // 6. Guardar
            FormularioReciclador nuevo = formularioRecicladorService.save(formularioReciclador);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno al guardar: " + e.getMessage()));
        }
    }

    // =================================================================
    // UPDATE (Mantenemos tu lógica original + Fotos y Vinculación)
    // =================================================================
    @PutMapping("/formularios_reciclador/{id}")
    public ResponseEntity<?> update(
            @RequestBody FormularioReciclador formularioReciclador,
            @PathVariable Long id) {

       FormularioReciclador formularioActual = formularioRecicladorService.findById(id);

        if (formularioActual == null)
            return ResponseEntity.notFound().build();

        Boolean aprobadoAntes = formularioActual.getAprobado();

        // Actualizar campos básicos
        formularioActual.setAnios_experiencia(formularioReciclador.getAnios_experiencia());
        formularioActual.setNombre_sitio(formularioReciclador.getNombre_sitio());
        formularioActual.setLatitud(formularioReciclador.getLatitud());
        formularioActual.setLongitud(formularioReciclador.getLongitud());
        formularioActual.setUbicacion(formularioReciclador.getUbicacion());
        
        // Mantener fotos si no vienen nuevas (o actualizarlas si el JSON las trae)
        if(formularioReciclador.getFoto_perfil_profesional() != null)
             formularioActual.setFoto_perfil_profesional(formularioReciclador.getFoto_perfil_profesional());
        if(formularioReciclador.getEvidencia_experiencia() != null)
             formularioActual.setEvidencia_experiencia(formularioReciclador.getEvidencia_experiencia());
             
        formularioActual.setAprobado(formularioReciclador.getAprobado());
        formularioActual.setObservacion_admin(formularioReciclador.getObservacion_admin());

        // Actualizar Horarios (Limpiar y agregar nuevos vinculados)
        if (formularioReciclador.getHorarios() != null) {
            formularioActual.getHorarios().clear();
            for (HorarioReciclador h : formularioReciclador.getHorarios()) {
                h.setFormulario(formularioActual); // Vincular
            }
            formularioActual.getHorarios().addAll(formularioReciclador.getHorarios());
        }
        
        // Actualizar Materiales (Limpiar y agregar nuevos vinculados)
        if (formularioReciclador.getMateriales() != null) {
            formularioActual.getMateriales().clear();
            for (FormularioRecicladorMaterial m : formularioReciclador.getMateriales()) {
                m.setFormulario(formularioActual); // Vincular
            }
            formularioActual.getMateriales().addAll(formularioReciclador.getMateriales());
        }

        FormularioReciclador actualizado = formularioRecicladorService.save(formularioActual);

        // Lógica de Notificaciones
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
                
                // OJO: Aquí deberías cambiar el ROL del usuario a 2 (Reciclador)
                // Usuario u = actualizado.getUsuario();
                // u.setRol(...); 
                // usuarioService.save(u);
                
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