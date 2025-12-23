package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.entity.Multimedia;
import com.example.demo.models.service.IMultimedioService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class MultimediaRestController {

    @Autowired
    private IMultimedioService multimedioService;

    // LISTAR TODO
    @GetMapping("/multimedias")
    public List<Multimedia> index() {
        return multimedioService.findAll();
    }

    // OBTENER UNO
    @GetMapping("/multimedias/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Multimedia multimedia = multimedioService.findById(id);
        if (multimedia == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(multimedia);
    }

    // ==========================================
    // CREAR (Recibe JSON con la foto ya en Base64)
    // ==========================================
    @PostMapping("/multimedias") 
    @ResponseStatus(HttpStatus.CREATED)
    public Multimedia create(@RequestBody Multimedia multimedia) {
        // El objeto ya llega con "imagenes" lleno de texto base64 desde el JS.
        // Solo guardamos.
        return multimedioService.save(multimedia);
    }

    // ==========================================
    // EDITAR (Recibe JSON)
    // ==========================================
    @PutMapping("/multimedias/{id}")
    public ResponseEntity<?> update(@RequestBody Multimedia multimedia, @PathVariable Long id) {
        Multimedia actual = multimedioService.findById(id);

        if (actual == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizamos datos básicos
        if (multimedia.getTitulo() != null) actual.setTitulo(multimedia.getTitulo());
        if (multimedia.getDescripcion() != null) actual.setDescripcion(multimedia.getDescripcion());

        // Solo actualizamos la imagen si el usuario mandó una nueva
        // (Si viene null o vacía, mantenemos la foto vieja)
        if (multimedia.getImagenes() != null && !multimedia.getImagenes().isEmpty()) {
            actual.setImagenes(multimedia.getImagenes());
        }

        Multimedia actualizado = multimedioService.save(actual);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================
    @DeleteMapping("/multimedias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        // Ya no hay que borrar archivos del disco, solo de la BD
        multimedioService.delete(id);
    }
}