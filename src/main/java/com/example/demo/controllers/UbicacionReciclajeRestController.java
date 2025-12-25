package com.example.demo.controllers;

import java.time.LocalDateTime;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // IMPORTANTE PARA LA HORA

import com.example.demo.models.entity.HorarioReciclador; // IMPORTANTE
import com.example.demo.models.entity.Notificacion;
import com.example.demo.models.entity.UbicacionMaterial; // IMPORTANTE
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
    
    // 1. CONFIGURAR MAPPER PARA HORAS (LocalTime)
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
    // CREAR (CON HORARIOS Y MATERIALES)
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
    
        // B. VINCULAR RECICLADOR (SI EXISTE)
        if (ubicacionReciclaje.getReciclador() != null && ubicacionReciclaje.getReciclador().getCedula() != null) {
            Usuario recicladorReal = usuarioService.findById(ubicacionReciclaje.getReciclador().getCedula());
            if (recicladorReal != null) {
                ubicacionReciclaje.setReciclador(recicladorReal);
            } else {
                ubicacionReciclaje.setReciclador(null);
            }
        }

        // C. VINCULAR HORARIOS (¡ESTO FALTABA!)
        if (ubicacionReciclaje.getHorarios() != null) {
            for (HorarioReciclador h : ubicacionReciclaje.getHorarios()) {
                h.setUbicacion(ubicacionReciclaje); // El hijo conoce al padre
            }
        }

        // D. VINCULAR MATERIALES (¡ESTO FALTABA!)
        if (ubicacionReciclaje.getMaterialesAceptados() != null) {
            for (UbicacionMaterial m : ubicacionReciclaje.getMaterialesAceptados()) {
                m.setUbicacion(ubicacionReciclaje); // El hijo conoce al padre
            }
        }

        // E. GUARDAR
        UbicacionReciclaje nuevo = ubicacionReciclajeService.save(ubicacionReciclaje);
        
        // F. NOTIFICAR
        notificarUsuariosNormales(nuevo);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
    
    private void notificarUsuariosNormales(UbicacionReciclaje nuevoPunto) {
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
            
            Long idRef = (nuevoPunto.getId_ubicacion_reciclaje() != null) 
                          ? nuevoPunto.getId_ubicacion_reciclaje() 
                          : nuevoPunto.getId_ubicacion_reciclaje(); 
            noti.setId_referencia(idRef);

            notificacionService.save(noti);
        }
        System.out.println("Se notificó a " + usuariosNormales.size() + " usuarios normales.");
    }

    // ======================================================================
    // ACTUALIZAR (CON HORARIOS Y MATERIALES)
    // ======================================================================
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

        if (ubicacionDatos.getParroquia() != null) {
            actualDB.setParroquia(ubicacionDatos.getParroquia());
        }

        // 3. RECICLADOR
        if (ubicacionDatos.getReciclador() != null && ubicacionDatos.getReciclador().getCedula() != null) {
            Usuario r = usuarioService.findById(ubicacionDatos.getReciclador().getCedula());
            actualDB.setReciclador(r);
        } else {
            actualDB.setReciclador(null);
        }

        // 4. ACTUALIZAR HORARIOS (Borrar viejos, poner nuevos)
        if (ubicacionDatos.getHorarios() != null) {
            actualDB.getHorarios().clear(); // Gracias a orphanRemoval = true, esto borra en BD
            for (HorarioReciclador h : ubicacionDatos.getHorarios()) {
                h.setUbicacion(actualDB); // Vincular
                actualDB.getHorarios().add(h);
            }
        }

        // 5. ACTUALIZAR MATERIALES (Borrar viejos, poner nuevos)
        if (ubicacionDatos.getMaterialesAceptados() != null) {
            // A. Inicializar la lista si viniera nula de la BD (para evitar NullPointerException)
            if (actualDB.getMaterialesAceptados() == null) {
                actualDB.setMaterialesAceptados(new java.util.ArrayList<>());
            }

            // B. Borrar los antiguos (gracias a orphanRemoval = true en la entidad, esto borra de BD)
            actualDB.getMaterialesAceptados().clear();

            // C. Agregar los nuevos "limpios"
            for (UbicacionMaterial mInput : ubicacionDatos.getMaterialesAceptados()) {
                // IMPORTANTE: Creamos una nueva instancia para asegurar que el estado es "New"
                UbicacionMaterial mNuevo = new UbicacionMaterial();
                
                // 1. Vinculamos al Padre (Ubicación)
                mNuevo.setUbicacion(actualDB);
                
                // 2. Vinculamos el Material (solo necesitamos el ID que viene en mInput)
                mNuevo.setMaterial(mInput.getMaterial()); 
                
                // 3. Agregamos a la lista gestionada
                actualDB.getMaterialesAceptados().add(mNuevo);
            }
        }

        UbicacionReciclaje actualizado = ubicacionReciclajeService.save(actualDB);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
    }

    @DeleteMapping("/ubicacion_reciclajes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ubicacionReciclajeService.delete(id);
    }
}