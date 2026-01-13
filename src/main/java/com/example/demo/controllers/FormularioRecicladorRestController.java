package com.example.demo.controllers;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.example.demo.models.entity.FormularioReciclador;
import com.example.demo.models.entity.FormularioRecicladorMaterial;
import com.example.demo.models.entity.HorarioReciclador;
import com.example.demo.models.entity.Notificacion;
import com.example.demo.models.entity.Rol;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.entity.UsuarioRol;
import com.example.demo.models.entity.UbicacionReciclaje;
import com.example.demo.models.entity.UbicacionMaterial; 
import com.example.demo.models.service.IFormularioRecicladorService;
import com.example.demo.models.service.INotificacionService;
import com.example.demo.models.service.IUsuarioService;
import com.example.demo.models.service.IRolService;
import com.example.demo.models.service.IUbicacionReciclajeService;
import com.example.demo.models.service.SupabaseStorageService;
import com.example.demo.models.service.IParroquiService; 
import com.example.demo.models.entity.Parroquia;

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
    private IRolService rolService;

    @Autowired
    private IUbicacionReciclajeService ubicacionReciclajeService;

    @Autowired
    private IParroquiService parroquiaService;
    
    @Autowired
    private SupabaseStorageService storageService;

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
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        try {
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                String urlFoto = storageService.subirImagen(fotoPerfil);
                formularioReciclador.setFoto_perfil_profesional(urlFoto);
            }
            if (evidencia != null && !evidencia.isEmpty()) {
                String urlEvidencia = storageService.subirImagen(evidencia);
                formularioReciclador.setEvidencia_experiencia(urlEvidencia);
            }

            if (formularioReciclador.getUsuario() != null) {
                Usuario u = usuarioService.findById(formularioReciclador.getUsuario().getCedula());
                if (u != null) formularioReciclador.setUsuario(u);
            }

            if (formularioReciclador.getHorarios() != null) {
                for (HorarioReciclador h : formularioReciclador.getHorarios()) {
                    h.setFormulario(formularioReciclador);
                }
            }
            
            if (formularioReciclador.getMateriales() != null) {
                for (FormularioRecicladorMaterial m : formularioReciclador.getMateriales()) {
                    m.setFormulario(formularioReciclador);
                }
            }

            FormularioReciclador nuevo = formularioRecicladorService.save(formularioReciclador);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno: " + e.getMessage()));
        }
    }


    private void crearUbicacionDesdeFormulario(FormularioReciclador form, Long idParroquiaExtra) {
        UbicacionReciclaje nuevaUbi = new UbicacionReciclaje();
        nuevaUbi.setNombre(form.getNombre_sitio());
        nuevaUbi.setDireccion(form.getUbicacion());
        nuevaUbi.setLatitud(form.getLatitud());
        nuevaUbi.setLongitud(form.getLongitud());
        nuevaUbi.setReciclador(form.getUsuario());
        nuevaUbi.setFoto(form.getFoto_perfil_profesional()); 
        

        if (idParroquiaExtra != null) {
            Parroquia p = parroquiaService.findById(idParroquiaExtra);
            if (p != null) nuevaUbi.setParroquia(p);
        } else if (form.getUsuario().getParroquia() != null) {
            nuevaUbi.setParroquia(form.getUsuario().getParroquia());
        }
        
        if (form.getMateriales() != null && !form.getMateriales().isEmpty()) {
            List<UbicacionMaterial> materialesParaUbi = new ArrayList<>();
            for (FormularioRecicladorMaterial matForm : form.getMateriales()) {
                UbicacionMaterial matUbi = new UbicacionMaterial();
                matUbi.setMaterial(matForm.getMaterial()); 
                matUbi.setUbicacion(nuevaUbi); 
                materialesParaUbi.add(matUbi);
            }
            nuevaUbi.setMaterialesAceptados(materialesParaUbi);
        }

        if (form.getHorarios() != null && !form.getHorarios().isEmpty()) {
            List<HorarioReciclador> horariosParaUbi = new ArrayList<>();
            
            for (HorarioReciclador hForm : form.getHorarios()) {
                HorarioReciclador hUbi = new HorarioReciclador();
                
                hUbi.setDia_semana(hForm.getDia_semana());
                hUbi.setHora_inicio(hForm.getHora_inicio());
                hUbi.setHora_fin(hForm.getHora_fin());
                
                hUbi.setUbicacion(nuevaUbi); 
                hUbi.setFormulario(null); 
                
                horariosParaUbi.add(hUbi);
            }
            nuevaUbi.setHorarios(horariosParaUbi);
        }

        ubicacionReciclajeService.save(nuevaUbi);
        System.out.println("Ubicación creada con materiales y horarios.");
    }

    private void asignarRolReciclador(Usuario u) {
        Rol rolReciclador = rolService.findById(2L);

        if (rolReciclador != null && u != null) {
            
            if (u.getRoles() == null) {
                u.setRoles(new ArrayList<>());
            }

            boolean yaTiene = u.getRoles().stream()
                .anyMatch(ur -> ur.getRol() != null && ur.getRol().getId_rol().equals(2L));

            if (!yaTiene) {
                UsuarioRol nuevoAsignacion = new UsuarioRol();
                
                nuevoAsignacion.setUsuario(u);        
                nuevoAsignacion.setRol(rolReciclador); 
                
                u.getRoles().add(nuevoAsignacion);

                usuarioService.save(u);
                
                System.out.println("Rol de Reciclador asignado a: " + u.getCedula());
            }
        }
    }

    private void crearNotificacionAprobacion(FormularioReciclador form) {
        Notificacion noti = new Notificacion();
        noti.setUsuario(form.getUsuario());
        noti.setFecha_creacion(LocalDateTime.now());
        noti.setLeido(false);
        noti.setTipo("FORMULARIO");
        noti.setEntidad_referencia("FORMULARIO");
        noti.setId_referencia(form.getId_formulario());
        noti.setTitulo("Solicitud aprobada");
        noti.setMensaje("Tu solicitud para ser reciclador fue aprobada. ¡Bienvenido a Loopi!");
        notificacionService.save(noti);
    }

    private void crearNotificacionRechazo(FormularioReciclador form) {
        Notificacion noti = new Notificacion();
        noti.setUsuario(form.getUsuario());
        noti.setFecha_creacion(LocalDateTime.now());
        noti.setLeido(false);
        noti.setTipo("FORMULARIO");
        noti.setEntidad_referencia("FORMULARIO");
        noti.setId_referencia(form.getId_formulario());
        noti.setTitulo("Solicitud rechazada");
        noti.setMensaje(form.getObservacion_admin() != null ? form.getObservacion_admin() : "Solicitud rechazada.");
        notificacionService.save(noti);
    }

    // Agrega este método a tu clase FormularioRecicladorRestController

    @PutMapping("/formularios_reciclador/{id}")
    public ResponseEntity<?> update(@RequestBody FormularioReciclador formularioDetails, @PathVariable Long id) {
        
        FormularioReciclador formularioActual = formularioRecicladorService.findById(id);

        if (formularioActual == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizamos los campos editables
        if(formularioDetails.getObservacion_admin() != null) {
            formularioActual.setObservacion_admin(formularioDetails.getObservacion_admin());
        }

        // Si desde el front envían aprobado = false, asumimos que es un rechazo
        if (formularioDetails.getAprobado() != null) {
            boolean estabaAprobado = formularioActual.getAprobado() != null && formularioActual.getAprobado();
            boolean nuevoEstado = formularioDetails.getAprobado();

            formularioActual.setAprobado(nuevoEstado);

            // Si se marca como NO aprobado (rechazo), enviamos la notificación
            if (!nuevoEstado) {
                crearNotificacionRechazo(formularioActual);
            }
        }

        formularioRecicladorService.save(formularioActual);
        
        return ResponseEntity.ok(formularioActual);
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
    public ResponseEntity<?> aprobar(@PathVariable Long id, @RequestBody Map<String, Object> body) { 
        FormularioReciclador form = formularioRecicladorService.findById(id);
        if(form == null) return ResponseEntity.notFound().build();
        
        form.setAprobado(true);
        form.setObservacion_admin((String) body.get("observacion_admin"));
        
        formularioRecicladorService.save(form);
        
        Long idParroquia = null;
        
        if (body.containsKey("usuario")) {
            Map<String, Object> usuarioMap = (Map<String, Object>) body.get("usuario");
            if (usuarioMap.containsKey("parroquia")) {
                Map<String, Object> parroquiaMap = (Map<String, Object>) usuarioMap.get("parroquia");
                if (parroquiaMap.containsKey("id_parroquia")) {
                    idParroquia = Long.valueOf(parroquiaMap.get("id_parroquia").toString());
                }
            }
        }
        
        asignarRolReciclador(form.getUsuario());
        crearUbicacionDesdeFormulario(form, idParroquia); 
        crearNotificacionAprobacion(form);

        return ResponseEntity.ok().body(Map.of("mensaje", "Formulario aprobado con éxito"));
    }
}