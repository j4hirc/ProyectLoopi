package com.example.demo.controllers;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList; // Necesario para las listas
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
// IMPORTACIONES NUEVAS PARA LA UBICACIÓN
import com.example.demo.models.entity.UbicacionReciclaje;
import com.example.demo.models.entity.UbicacionMaterial; 
import com.example.demo.models.service.IFormularioRecicladorService;
import com.example.demo.models.service.INotificacionService;
import com.example.demo.models.service.IUsuarioService;
import com.example.demo.models.service.IRolService;
import com.example.demo.models.service.IUbicacionReciclajeService; // Servicio Ubicación
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
    private IRolService rolService;

    // 1. INYECTAR SERVICIO DE UBICACIONES
    @Autowired
    private IUbicacionReciclajeService ubicacionReciclajeService;

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

    // CREATE (Sin cambios mayores, solo guarda el formulario)
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

    // =================================================================
    // UPDATE: AQUÍ SE CREA LA UBICACIÓN Y SE PASAN LOS MATERIALES
    // =================================================================
    @PutMapping(value = "/ubicacion_reciclajes/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        UbicacionReciclaje ubicacionDatos;
        try {
            ubicacionDatos = objectMapper.readValue(datosJson, UbicacionReciclaje.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }
        
        UbicacionReciclaje actualDB = ubicacionReciclajeService.findById(id);
        if (actualDB == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            actualDB.setFoto(urlImagen);
        }

        actualDB.setNombre(ubicacionDatos.getNombre());
        actualDB.setDireccion(ubicacionDatos.getDireccion());
        actualDB.setLatitud(ubicacionDatos.getLatitud());
        actualDB.setLongitud(ubicacionDatos.getLongitud());

        if (ubicacionDatos.getParroquia() != null) {
            actualDB.setParroquia(ubicacionDatos.getParroquia());
        }

        if (ubicacionDatos.getReciclador() != null && ubicacionDatos.getReciclador().getCedula() != null) {
            Usuario r = usuarioService.findById(ubicacionDatos.getReciclador().getCedula());
            actualDB.setReciclador(r);
        } else {
            actualDB.setReciclador(null);
        }

        // 4. ACTUALIZAR HORARIOS (PRESERVANDO ID_FORMULARIO)
        if (ubicacionDatos.getHorarios() != null) {
            
            // A. Intentamos rescatar el formulario original de los horarios viejos
            FormularioReciclador formularioOriginal = null;
            if (actualDB.getHorarios() != null && !actualDB.getHorarios().isEmpty()) {
                // Tomamos el formulario del primer horario que encontremos (asumiendo que todos vienen del mismo)
                for(HorarioReciclador hViejo : actualDB.getHorarios()) {
                    if(hViejo.getFormulario() != null) {
                        formularioOriginal = hViejo.getFormulario();
                        break; 
                    }
                }
            }

            // B. Limpiamos la lista (esto borra los registros viejos)
            actualDB.getHorarios().clear(); 
            
            // C. Agregamos los nuevos, inyectándoles el formulario rescatado
            for (HorarioReciclador h : ubicacionDatos.getHorarios()) {
                h.setUbicacion(actualDB); // Vincular Ubicación (Padre principal)
                
                // Si existía un formulario original, lo volvemos a vincular
                if (formularioOriginal != null) {
                    h.setFormulario(formularioOriginal);
                }
                
                actualDB.getHorarios().add(h);
            }
        }

        // 5. ACTUALIZAR MATERIALES
        if (ubicacionDatos.getMaterialesAceptados() != null) {
            actualDB.getMaterialesAceptados().clear(); 
            for (UbicacionMaterial m : ubicacionDatos.getMaterialesAceptados()) {
                m.setUbicacion(actualDB); 
                actualDB.getMaterialesAceptados().add(m);
            }
        }

        UbicacionReciclaje actualizado = ubicacionReciclajeService.save(actualDB);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
    }
    // --- MÉTODOS AUXILIARES PARA ORDENAR EL CÓDIGO ---

    private void crearUbicacionDesdeFormulario(FormularioReciclador form) {
        // 1. Crear Ubicación Base
        UbicacionReciclaje nuevaUbi = new UbicacionReciclaje();
        nuevaUbi.setNombre(form.getNombre_sitio());
        nuevaUbi.setDireccion(form.getUbicacion());
        nuevaUbi.setLatitud(form.getLatitud());
        nuevaUbi.setLongitud(form.getLongitud());
        nuevaUbi.setReciclador(form.getUsuario());
        nuevaUbi.setFoto(form.getFoto_perfil_profesional()); 
        
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

        // 4. Guardar (CascadeType.ALL guardará materiales y horarios)
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
                
                nuevoAsignacion.setUsuario(u);        // Vinculamos al Usuario
                nuevoAsignacion.setRol(rolReciclador); // Vinculamos al Rol
                
                // 4. Agregamos el UsuarioRol a la lista del usuario
                u.getRoles().add(nuevoAsignacion);

                // 5. Guardamos (El CascadeType.ALL en Usuario guardará el UsuarioRol)
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
    
    // ... Resto de métodos (buscarPorUsuario, aprobar) siguen igual ...
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
        // Para consistencia, redirigimos la lógica al método update o replicamos:
        FormularioReciclador form = formularioRecicladorService.findById(id);
        if(form == null) return ResponseEntity.notFound().build();
        
        form.setAprobado(true);
        form.setObservacion_admin(body.get("observacion_admin"));
        
        // Guardamos y desencadenamos todo
        // Ojo: Aquí deberías llamar a las funciones auxiliares (roles, notificacion, ubicación)
        // O simplemente dejar que el frontend llame a 'update'
        
        formularioRecicladorService.save(form);
        // LLAMADA MANUAL A LO QUE HICIMOS ARRIBA
        asignarRolReciclador(form.getUsuario());
        crearUbicacionDesdeFormulario(form);
        crearNotificacionAprobacion(form);

        return ResponseEntity.ok().body(Map.of("mensaje", "Formulario aprobado con éxito"));
    }
}