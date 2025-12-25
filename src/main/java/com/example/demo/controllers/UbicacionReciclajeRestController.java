package com.example.demo.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList; 
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; 

import com.example.demo.models.entity.FormularioReciclador; // <--- IMPORTANTE: Agregado
import com.example.demo.models.entity.HorarioReciclador;
import com.example.demo.models.entity.Material;
import com.example.demo.models.entity.Notificacion;
import com.example.demo.models.entity.UbicacionMaterial;
import com.example.demo.models.entity.UbicacionReciclaje;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.service.INotificacionService;
import com.example.demo.models.service.IUbicacionReciclajeService;
import com.example.demo.models.service.IUsuarioService;
import com.example.demo.models.service.SupabaseStorageService;

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

 
    @PutMapping(value = "/ubicacion_reciclajes/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestParam("datos") String datosJson, @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
        try {
            UbicacionReciclaje datosFrontend = objectMapper.readValue(datosJson, UbicacionReciclaje.class);
            UbicacionReciclaje actualDB = ubicacionReciclajeService.findById(id);
            
            if (actualDB == null) return ResponseEntity.notFound().build();

            // 1. Actualizar Datos Básicos
            actualDB.setNombre(datosFrontend.getNombre());
            actualDB.setDireccion(datosFrontend.getDireccion());
            actualDB.setLatitud(datosFrontend.getLatitud());
            actualDB.setLongitud(datosFrontend.getLongitud());
            actualDB.setParroquia(datosFrontend.getParroquia());

            // 2. Actualizar Foto (solo si viene nueva)
            if (archivo != null && !archivo.isEmpty()) actualDB.setFoto(storageService.subirImagen(archivo));

            // 3. Actualizar Reciclador
            if (datosFrontend.getReciclador() != null && datosFrontend.getReciclador().getCedula() != null) {
                actualDB.setReciclador(usuarioService.findById(datosFrontend.getReciclador().getCedula()));
            } else {
                actualDB.setReciclador(null);
            }

            // 4. ACTUALIZAR HORARIOS (Borrar y Reemplazar)
            if (actualDB.getHorarios() == null) actualDB.setHorarios(new ArrayList<>());
            actualDB.getHorarios().clear(); // Borra viejos
            if (datosFrontend.getHorarios() != null) {
                for (HorarioReciclador h : datosFrontend.getHorarios()) {
                    h.setUbicacion(actualDB); // Vincular
                    actualDB.getHorarios().add(h);
                }
            }

            // 5. ACTUALIZAR MATERIALES (Borrar y Reemplazar)
            if (actualDB.getMaterialesAceptados() == null) actualDB.setMaterialesAceptados(new ArrayList<>());
            actualDB.getMaterialesAceptados().clear(); // Borra viejos
            if (datosFrontend.getMaterialesAceptados() != null) {
                for (UbicacionMaterial m : datosFrontend.getMaterialesAceptados()) {
                    if(m.getMaterial() != null && m.getMaterial().getId_material() != null) {
                        // Crear Referencia Limpia (Truco Anti-Error)
                        Material matRef = new Material();
                        matRef.setId_material(m.getMaterial().getId_material());
                        
                        UbicacionMaterial nuevoRelacion = new UbicacionMaterial();
                        nuevoRelacion.setUbicacion(actualDB);
                        nuevoRelacion.setMaterial(matRef);
                        
                        actualDB.getMaterialesAceptados().add(nuevoRelacion);
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionReciclajeService.save(actualDB));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/ubicacion_reciclajes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ubicacionReciclajeService.delete(id);
    }
}