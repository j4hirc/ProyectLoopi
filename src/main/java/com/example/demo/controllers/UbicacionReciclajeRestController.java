package com.example.demo.controllers;

import java.util.List;
import java.util.Map; // Necesario para respuestas de error JSON

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // Necesario para FormData
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Necesario para el archivo

import com.fasterxml.jackson.databind.ObjectMapper; // Necesario para convertir String a Objeto

import com.example.demo.models.entity.UbicacionReciclaje;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.service.IUbicacionReciclajeService;
import com.example.demo.models.service.IUsuarioService;
import com.example.demo.models.service.SupabaseStorageService; // Tu servicio de fotos

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UbicacionReciclajeRestController {
	
	@Autowired
    private IUsuarioService usuarioService;
	
	@Autowired
	private IUbicacionReciclajeService ubicacionReciclajeService;

    // 1. INYECCIÓN PARA FOTOS Y JSON
    @Autowired
    private SupabaseStorageService storageService;
    
    private ObjectMapper objectMapper = new ObjectMapper();

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

	@PostMapping(value = "/ubicacion_reciclajes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        UbicacionReciclaje ubicacionReciclaje;
        try {
            // A. Convertimos el texto JSON al Objeto
            ubicacionReciclaje = objectMapper.readValue(datosJson, UbicacionReciclaje.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

        // B. LÓGICA DE FOTO (Solo esto agregué)
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            ubicacionReciclaje.setFoto(urlImagen); 
        }
   
        // C. TU LÓGICA ORIGINAL (Intacta)
        if (ubicacionReciclaje.getReciclador() != null && ubicacionReciclaje.getReciclador().getCedula() != null) {
            Usuario recicladorReal = usuarioService.findById(ubicacionReciclaje.getReciclador().getCedula());
            if (recicladorReal != null) {
                ubicacionReciclaje.setReciclador(recicladorReal);
            } else {
                ubicacionReciclaje.setReciclador(null);
            }
        }

        if (ubicacionReciclaje.getParroquia() != null && ubicacionReciclaje.getParroquia().getId_parroquia() != null) {
             // ... lógica similar para buscar la parroquia real ...
        }

        UbicacionReciclaje nuevo = ubicacionReciclajeService.save(ubicacionReciclaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // ======================================================================
    // ACTUALIZAR (MODIFICADO PARA FOTOS)
    // ======================================================================
	@PutMapping(value = "/ubicacion_reciclajes/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        UbicacionReciclaje ubicacionReciclaje;
        try {
            // A. Convertimos el texto JSON al Objeto
            ubicacionReciclaje = objectMapper.readValue(datosJson, UbicacionReciclaje.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }
		
        // B. Buscar existente para no perder la foto vieja si no envían nueva
        UbicacionReciclaje actualDB = ubicacionReciclajeService.findById(id);
        if (actualDB != null && (archivo == null || archivo.isEmpty())) {
            // Si no mandan foto nueva, mantenemos la que ya tenía
            ubicacionReciclaje.setFoto(actualDB.getFoto());
        }

        // C. LÓGICA DE FOTO NUEVA
        if (archivo != null && !archivo.isEmpty()) {
            String urlImagen = storageService.subirImagen(archivo);
            ubicacionReciclaje.setFoto(urlImagen);
        }

		ubicacionReciclaje.setId_ubicacion_reciclaje(id);

        // D. TU LÓGICA ORIGINAL (Intacta)
		if (ubicacionReciclaje.getReciclador() != null && ubicacionReciclaje.getReciclador().getCedula() != null) {
			Usuario recicladorReal = usuarioService.findById(ubicacionReciclaje.getReciclador().getCedula());
			if (recicladorReal != null) {
				ubicacionReciclaje.setReciclador(recicladorReal);
			} else {
				ubicacionReciclaje.setReciclador(null);
			}
		}

		if (ubicacionReciclaje.getParroquia() != null && ubicacionReciclaje.getParroquia().getId_parroquia() != null) {
		}

		UbicacionReciclaje actualizado = ubicacionReciclajeService.save(ubicacionReciclaje);
		
		if (actualizado == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
	}

	@DeleteMapping("/ubicacion_reciclajes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		ubicacionReciclajeService.delete(id);
	}
}