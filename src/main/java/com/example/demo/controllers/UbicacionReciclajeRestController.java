	package com.example.demo.controllers;
	
	import java.util.List;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.http.HttpStatus;
	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.annotation.*;
	
	import com.example.demo.models.entity.UbicacionReciclaje;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.service.IUbicacionReciclajeService;
import com.example.demo.models.service.IUsuarioService;
	
	@RestController
	@RequestMapping("/api")
	@CrossOrigin(origins = "*")
	public class UbicacionReciclajeRestController {
		
		@Autowired
	    private IUsuarioService usuarioService;
		
		@Autowired
		private IUbicacionReciclajeService ubicacionReciclajeService;
	
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
	
		@PostMapping("/ubicacion_reciclajes")
	    @ResponseStatus(HttpStatus.CREATED)
	    public UbicacionReciclaje create(@RequestBody UbicacionReciclaje ubicacionReciclaje) {
	        
	   
	        if (ubicacionReciclaje.getReciclador() != null && ubicacionReciclaje.getReciclador().getCedula() != null) {
	            
	            // 2. Buscar el usuario REAL en la base de datos
	            Usuario recicladorReal = usuarioService.findById(ubicacionReciclaje.getReciclador().getCedula());
	            
	            // 3. Reemplazar el objeto "falso" del JSON por el objeto REAL de la BD
	            if (recicladorReal != null) {
	                ubicacionReciclaje.setReciclador(recicladorReal);
	            } else {
	                // Opcional: Si no existe, dejarlo en null para que no falle o lanzar error
	                ubicacionReciclaje.setReciclador(null);
	            }
	        }

	        // Lo mismo aplica para la Parroquia si te da error similar
	        if (ubicacionReciclaje.getParroquia() != null && ubicacionReciclaje.getParroquia().getId_parroquia() != null) {
	             // ... lógica similar para buscar la parroquia real ...
	        }

	        return ubicacionReciclajeService.save(ubicacionReciclaje);
	    }
	
		@PutMapping("/ubicacion_reciclajes/{id}")
		public ResponseEntity<?> update(@RequestBody UbicacionReciclaje ubicacionReciclaje, @PathVariable Long id) {
			
			ubicacionReciclaje.setId_ubicacion_reciclaje(id);

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