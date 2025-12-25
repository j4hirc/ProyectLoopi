package com.example.demo.controllers;

import java.util.ArrayList; 
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; 

import com.example.demo.models.entity.FormularioReciclador;
import com.example.demo.models.entity.HorarioReciclador;
import com.example.demo.models.entity.Material;
import com.example.demo.models.entity.UbicacionMaterial;
import com.example.demo.models.entity.UbicacionReciclaje;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.service.INotificacionService;
import com.example.demo.models.service.IUbicacionReciclajeService;
import com.example.demo.models.service.IUsuarioService;
import com.example.demo.models.service.SupabaseStorageService;
// You might need to import Notificacion entity if you use it in the notify method
import com.example.demo.models.entity.Notificacion; 
import java.time.LocalDateTime;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UbicacionReciclajeRestController {
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private IUbicacionReciclajeService ubicacionReciclajeService;

    @Autowired
    private SupabaseStorageService storageService;
    
    @Autowired
    private INotificacionService notificacionService;
    
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @GetMapping("/ubicacion_reciclajes")
    public List<UbicacionReciclaje> index() {
        return ubicacionReciclajeService.findAll();
    }

    @GetMapping("/ubicacion_reciclajes/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        UbicacionReciclaje ubicacion = ubicacionReciclajeService.findById(id);
        if (ubicacion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ubicacion);
    }

    // =================================================================
    // CREAR 
    // =================================================================
    @PostMapping(value = "/ubicacion_reciclajes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        UbicacionReciclaje ubicacionReciclaje;
        try {
            ubicacionReciclaje = objectMapper.readValue(datosJson, UbicacionReciclaje.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        // A. FOTO
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            ubicacionReciclaje.setFoto(urlImagen); 
        }
    
        // B. VINCULAR RECICLADOR
        if (ubicacionReciclaje.getReciclador() != null && ubicacionReciclaje.getReciclador().getCedula() != null) {
            Usuario recicladorReal = usuarioService.findById(ubicacionReciclaje.getReciclador().getCedula());
            if (recicladorReal != null) {
                ubicacionReciclaje.setReciclador(recicladorReal);
            } else {
                ubicacionReciclaje.setReciclador(null);
            }
        }

        // C. VINCULAR HORARIOS
        if (ubicacionReciclaje.getHorarios() != null) {
            for (HorarioReciclador h : ubicacionReciclaje.getHorarios()) {
                h.setUbicacion(ubicacionReciclaje); 
            }
        }

        // D. VINCULAR MATERIALES
        if (ubicacionReciclaje.getMaterialesAceptados() != null) {
            for (UbicacionMaterial m : ubicacionReciclaje.getMaterialesAceptados()) {
                m.setUbicacion(ubicacionReciclaje); 
            }
        }

        UbicacionReciclaje nuevo = ubicacionReciclajeService.save(ubicacionReciclaje);
        
        notificarUsuariosNormales(nuevo);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
    
    private void notificarUsuariosNormales(UbicacionReciclaje nuevoPunto) {
        try {
            List<Usuario> todos = usuarioService.findAll();
            List<Usuario> usuariosNormales = todos.stream()
                .filter(u -> u.getRoles() != null && u.getRoles().stream()
                    .anyMatch(usuarioRol -> 
                        usuarioRol.getRol() != null && 
                        usuarioRol.getRol().getId_rol() == 1L 
                    )) 
                .collect(Collectors.toList());

            String titulo = "Nuevo Punto de Reciclaje ♻️";
            String mensaje = "Se ha habilitado: " + nuevoPunto.getNombre() + ". ¡Míralo en el mapa!";

            for (Usuario u : usuariosNormales) {
                Notificacion noti = new Notificacion();
                noti.setUsuario(u);
                noti.setTitulo(titulo);
                noti.setMensaje(mensaje);
                noti.setFecha_creacion(LocalDateTime.now());
                noti.setLeido(false);
                noti.setTipo("SISTEMA");
                noti.setEntidad_referencia("UBICACION");
                
                Long idRef = nuevoPunto.getId_ubicacion_reciclaje(); 
                noti.setId_referencia(idRef);

                notificacionService.save(noti);
            }
        } catch (Exception e) {
            System.out.println("Error enviando notificaciones (no crítico): " + e.getMessage());
        }
    }

    // ======================================================================
    // ACTUALIZAR (CORREGIDO PARA MATERIALES)
    // ======================================================================
    
    @PutMapping(value = "/ubicacion_reciclajes/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional // Mantiene la sesión de hibernate viva
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam("datos") String datosJson,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
        
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

        // 1. FOTO
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            actualDB.setFoto(urlImagen);
        }

        // 2. DATOS BÁSICOS
        actualDB.setNombre(ubicacionDatos.getNombre());
        actualDB.setDireccion(ubicacionDatos.getDireccion());
        actualDB.setLatitud(ubicacionDatos.getLatitud());
        actualDB.setLongitud(ubicacionDatos.getLongitud());

        if (ubicacionDatos.getParroquia() != null)
            actualDB.setParroquia(ubicacionDatos.getParroquia());

        // 3. RECICLADOR
        if (ubicacionDatos.getReciclador() != null && ubicacionDatos.getReciclador().getCedula() != null) {
            Usuario r = usuarioService.findById(ubicacionDatos.getReciclador().getCedula());
            actualDB.setReciclador(r);
        } else {
            actualDB.setReciclador(null);
        }

        // 4. ACTUALIZAR HORARIOS
        if (ubicacionDatos.getHorarios() != null) {
            if (actualDB.getHorarios() == null)
                actualDB.setHorarios(new ArrayList<>());

            // Rescatar formulario para no perder la referencia
            FormularioReciclador formularioOriginal = null;
            if (!actualDB.getHorarios().isEmpty()) {
                for (HorarioReciclador h : actualDB.getHorarios()) {
                    if (h.getFormulario() != null) {
                        formularioOriginal = h.getFormulario();
                        break;
                    }
                }
            }

            actualDB.getHorarios().clear();
            for (HorarioReciclador h : ubicacionDatos.getHorarios()) {
                h.setUbicacion(actualDB);
                if (formularioOriginal != null)
                    h.setFormulario(formularioOriginal);
                actualDB.getHorarios().add(h);
            }
        }

        // 5. ACTUALIZAR MATERIALES (ESTRATEGIA DOBLE SAVE)
        // Esta lógica asegura que se borren los viejos y se inserten los nuevos sin conflicto
        if (ubicacionDatos.getMaterialesAceptados() != null) {

            // PASO A: Limpiar la lista actual en memoria
            if (actualDB.getMaterialesAceptados() != null) {
                actualDB.getMaterialesAceptados().clear();
            } else {
                actualDB.setMaterialesAceptados(new ArrayList<>());
            }

            // PASO B: Guardar PRIMERO el estado vacío.
            // Esto obliga a la BD a ejecutar los DELETE inmediatamente.
            actualDB = ubicacionReciclajeService.save(actualDB);

            // PASO C: Crear la lista de nuevos materiales
            List<UbicacionMaterial> nuevosMateriales = new ArrayList<>();

            for (UbicacionMaterial mInput : ubicacionDatos.getMaterialesAceptados()) {
                if (mInput.getMaterial() != null && mInput.getMaterial().getId_material() != null) {

                    UbicacionMaterial nuevoRelacion = new UbicacionMaterial();

                    // 1. Vincular al Padre (actualDB ya está refrescado por el save anterior)
                    nuevoRelacion.setUbicacion(actualDB);

                    // 2. Vincular al Material (Creamos referencia limpia con solo el ID)
                    Material materialRef = new Material();
                    materialRef.setId_material(mInput.getMaterial().getId_material());
                    nuevoRelacion.setMaterial(materialRef);

                    nuevosMateriales.add(nuevoRelacion);
                }
            }

            // PASO D: Agregar los nuevos a la lista gestionada
            actualDB.getMaterialesAceptados().addAll(nuevosMateriales);
        }

        // GUARDADO FINAL (Esto ejecutará los INSERT)
        UbicacionReciclaje actualizado = ubicacionReciclajeService.save(actualDB);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
    }

    @DeleteMapping("/ubicacion_reciclajes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ubicacionReciclajeService.delete(id);
    }
}