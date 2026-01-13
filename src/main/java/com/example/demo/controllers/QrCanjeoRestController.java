package com.example.demo.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.entity.QR_Canje;
import com.example.demo.models.entity.Recompensa;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.service.IQrCanjeService;
import com.example.demo.models.service.IRecompensaService;
import com.example.demo.models.service.IUsuarioService;

class ValidacionRequest {
    public String token;
    public String codigo;
}

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QrCanjeoRestController {
	
	@Autowired
	private IQrCanjeService qrCanjeService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IRecompensaService recompensaService;

	@GetMapping("/qr_canjeos")
	public List<QR_Canje> index() {
		return qrCanjeService.findAll();
	}

	@GetMapping("/qr_canjeos/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		QR_Canje qr = qrCanjeService.findById(id);
		if (qr == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(qr);
	}
	
	@GetMapping("/qr_canjeos/usuario/{cedula}")
    public ResponseEntity<?> obtenerPorUsuario(@PathVariable Long cedula) {
        List<QR_Canje> misCupones = qrCanjeService.findAll().stream()
            .filter(qr -> qr.getUsuario().getCedula().equals(cedula))
            .sorted((c1, c2) -> c2.getFecha_generado().compareTo(c1.getFecha_generado())) 
            .toList(); 

        return ResponseEntity.ok(misCupones);
    }


	@PostMapping("/qr_canjeos")
	public ResponseEntity<?> create(@RequestBody QR_Canje datosEntrada) {
		
		if (datosEntrada.getUsuario() == null || datosEntrada.getUsuario().getCedula() == null ||
			datosEntrada.getRecompensa() == null || datosEntrada.getRecompensa().getId_recompensa() == null) {
			return ResponseEntity.badRequest().body(Map.of("mensaje", "Faltan datos del usuario o recompensa"));
		}

		Usuario usuario = usuarioService.findById(datosEntrada.getUsuario().getCedula());
		Recompensa recompensa = recompensaService.findById(datosEntrada.getRecompensa().getId_recompensa());

		if (usuario == null || recompensa == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario o Recompensa no encontrados"));
		}

		if (usuario.getPuntos_actuales() < recompensa.getCostoPuntos()) {
			return ResponseEntity.badRequest().body(Map.of("mensaje", "No tienes puntos suficientes"));
		}

		usuario.setPuntos_actuales(usuario.getPuntos_actuales() - recompensa.getCostoPuntos());
		usuarioService.save(usuario);

		QR_Canje nuevoCanje = new QR_Canje();
		nuevoCanje.setUsuario(usuario);
		nuevoCanje.setRecompensa(recompensa);
		nuevoCanje.setUsado(false);
		nuevoCanje.setToken(UUID.randomUUID().toString()); 

		QR_Canje canjeGuardado = qrCanjeService.save(nuevoCanje);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(canjeGuardado);
	}

	@PutMapping("/qr_canjeos/validar")
    public ResponseEntity<?> validarCanje(@RequestBody ValidacionRequest request) {
        
        QR_Canje qr = qrCanjeService.findByToken(request.token);

        if (qr == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "El código QR no existe."));
        }


        if (Boolean.TRUE.equals(qr.getUsado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT) 
                    .body(Map.of("mensaje", "Este código ya fue utilizado."));
        }

        String codigoDueño = qr.getRecompensa().getAuspiciante().getCodigo();
        
        if (codigoDueño == null || !codigoDueño.equalsIgnoreCase(request.codigo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN) 
                    .body(Map.of(
                        "mensaje", "Código de local incorrecto.",
                        "error", "Este premio pertenece a: " + qr.getRecompensa().getAuspiciante().getNombre()
                    ));
        }


        qr.setUsado(true);
        qr.setFecha_usado(LocalDateTime.now());
        QR_Canje actualizado = qrCanjeService.save(qr);

        return ResponseEntity.ok(Map.of(
            "mensaje", "Canje exitoso",
            "recompensa_titulo", actualizado.getRecompensa().getNombre(),
            "recompensa_desc", actualizado.getRecompensa().getDescripcion() != null ? actualizado.getRecompensa().getDescripcion() : "",
            "usuario_nombre", actualizado.getUsuario().getPrimer_nombre() + " " + actualizado.getUsuario().getApellido_paterno(),
            "usuario_foto", actualizado.getUsuario().getFoto() != null ? actualizado.getUsuario().getFoto() : ""
        ));
    }

	@DeleteMapping("/qr_canjeos/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		qrCanjeService.delete(id);
	}
}