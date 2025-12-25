package com.example.demo.controllers;

import java.time.LocalDateTime; // Para la fecha
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.entity.Notificacion; // Importar
import com.example.demo.models.entity.Recompensa;
import com.example.demo.models.entity.Usuario; // Importar
import com.example.demo.models.service.INotificacionService; // Importar
import com.example.demo.models.service.IRecompensaService;
import com.example.demo.models.service.IUsuarioService; // Importar

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class RecompensaRestController {
	
	@Autowired
	private IRecompensaService recompensaService;

    // 1. INYECTAMOS SERVICIOS PARA NOTIFICAR
    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private INotificacionService notificacionService;

	@GetMapping("/recompensas")
	public List<Recompensa> index() {
		return recompensaService.findAll();
	}

	@GetMapping("/recompensas/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Recompensa recompensa = recompensaService.findById(id);
		if (recompensa == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(recompensa);
	}


	@PostMapping("/recompensas")
	@ResponseStatus(HttpStatus.CREATED)
	public Recompensa create(@RequestBody Recompensa recompensa) {
		Recompensa nueva = recompensaService.save(recompensa);

        // 2. Notificamos a la gente
        notificarUsuariosNormales(nueva);

		return nueva;
	}

    private void notificarUsuariosNormales(Recompensa nuevaRecompensa) {
        List<Usuario> todos = usuarioService.findAll();

        List<Usuario> usuariosNormales = todos.stream()
            .filter(u -> u.getRoles() != null && u.getRoles().stream()
                .anyMatch(ur -> ur.getRol() != null && ur.getRol().getId_rol() == 1L)) 
            .collect(Collectors.toList());

        String titulo = "¡Nueva Recompensa! 🎁";
        String mensaje = "Canjea '" + nuevaRecompensa.getNombre() + "' por " + nuevaRecompensa.getCostoPuntos() + " puntos.";

        for (Usuario u : usuariosNormales) {
            Notificacion noti = new Notificacion();
            noti.setUsuario(u);
            noti.setTitulo(titulo);
            noti.setMensaje(mensaje);
            noti.setFecha_creacion(LocalDateTime.now());
            noti.setLeido(false);
            noti.setTipo("PROMOCION"); 
            noti.setEntidad_referencia("CANJEO");
            noti.setId_referencia(nuevaRecompensa.getId_recompensa());

            notificacionService.save(noti);
        }
        System.out.println("Se notificó la nueva recompensa a " + usuariosNormales.size() + " usuarios.");
    }

	@PutMapping("/recompensas/{id}")
	public ResponseEntity<?> update(@RequestBody Recompensa recompensa, @PathVariable Long id) {
		Recompensa recompensaActual = recompensaService.findById(id);

		if (recompensaActual == null) {
			return ResponseEntity.notFound().build();
		}

		recompensaActual.setNombre(recompensa.getNombre());
		recompensaActual.setDescripcion(recompensa.getDescripcion());
		recompensaActual.setCostoPuntos(recompensa.getCostoPuntos());
		
		recompensaActual.setDireccion(recompensa.getDireccion());
		recompensaActual.setLatitud(recompensa.getLatitud());
		recompensaActual.setLongitud(recompensa.getLongitud());

		recompensaActual.setAuspiciante(recompensa.getAuspiciante());

		Recompensa actualizada = recompensaService.save(recompensaActual);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(actualizada);
	}

	@DeleteMapping("/recompensas/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		recompensaService.delete(id);
	}
}