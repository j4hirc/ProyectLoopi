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

        if (ubicacionDatos.getParroquia() != null) actualDB.setParroquia(ubicacionDatos.getParroquia());

        // 3. RECICLADOR
        if (ubicacionDatos.getReciclador() != null && ubicacionDatos.getReciclador().getCedula() != null) {
            Usuario r = usuarioService.findById(ubicacionDatos.getReciclador().getCedula());
            actualDB.setReciclador(r);
        } else {
            actualDB.setReciclador(null);
        }

        // 4. HORARIOS
        if (ubicacionDatos.getHorarios() != null) {
            if (actualDB.getHorarios() == null) actualDB.setHorarios(new ArrayList<>());
            
            // Rescatar formulario
            FormularioReciclador formularioOriginal = null;
            if (!actualDB.getHorarios().isEmpty()) {
                for(HorarioReciclador h : actualDB.getHorarios()) {
                    if (h.getFormulario() != null) { formularioOriginal = h.getFormulario(); break; }
                }
            }
            
            actualDB.getHorarios().clear();
            for (HorarioReciclador h : ubicacionDatos.getHorarios()) {
                h.setUbicacion(actualDB);
                if (formularioOriginal != null) h.setFormulario(formularioOriginal);
                actualDB.getHorarios().add(h);
            }
        }

        // 5. MATERIALES (ESTA ES LA LÓGICA CORREGIDA)
        if (ubicacionDatos.getMaterialesAceptados() != null) {
            
            // A. Asegurar que la lista existe en BD
            if (actualDB.getMaterialesAceptados() == null) {
                actualDB.setMaterialesAceptados(new ArrayList<>());
            }
            
            // B. Crear una lista TEMPORAL con los nuevos (para no confundir a Hibernate)
            List<UbicacionMaterial> nuevosParaGuardar = new ArrayList<>();

            for (UbicacionMaterial mInput : ubicacionDatos.getMaterialesAceptados()) {
                // Validación estricta
                if (mInput.getMaterial() == null || mInput.getMaterial().getId_material() == null) {
                    continue; 
                }

                // Crear referencia limpia
                Material materialRef = new Material();
                materialRef.setId_material(mInput.getMaterial().getId_material());

                UbicacionMaterial mNuevo = new UbicacionMaterial();
                mNuevo.setUbicacion(actualDB); // Vinculamos al Padre
                mNuevo.setMaterial(materialRef); // Vinculamos el ID del Material
                
                nuevosParaGuardar.add(mNuevo);
            }

            // C. Operación Atómica: Borrar Todo y Agregar Todo
            actualDB.getMaterialesAceptados().clear();
            actualDB.getMaterialesAceptados().addAll(nuevosParaGuardar);
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