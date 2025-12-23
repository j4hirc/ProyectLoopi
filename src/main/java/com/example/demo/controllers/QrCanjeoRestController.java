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

// DTO Actualizado para recibir String
class ValidacionRequest {
    public String token;
    public String codigo; // <--- AHORA ES STRING (El código secreto del local)
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
		
		// 1. Validar inputs
		if (datosEntrada.getUsuario() == null || datosEntrada.getUsuario().getCedula() == null ||
			datosEntrada.getRecompensa() == null || datosEntrada.getRecompensa().getId_recompensa() == null) {
			return ResponseEntity.badRequest().body(Map.of("mensaje", "Faltan datos del usuario o recompensa"));
		}

		// 2. Buscar en BD
		Usuario usuario = usuarioService.findById(datosEntrada.getUsuario().getCedula());
		Recompensa recompensa = recompensaService.findById(datosEntrada.getRecompensa().getId_recompensa());

		if (usuario == null || recompensa == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario o Recompensa no encontrados"));
		}

		// 3. Validar Puntos
		if (usuario.getPuntos_actuales() < recompensa.getCostoPuntos()) {
			return ResponseEntity.badRequest().body(Map.of("mensaje", "No tienes puntos suficientes"));
		}

		// 4. Descontar puntos
		usuario.setPuntos_actuales(usuario.getPuntos_actuales() - recompensa.getCostoPuntos());
		usuarioService.save(usuario);

		// 5. Generar QR
		QR_Canje nuevoCanje = new QR_Canje();
		nuevoCanje.setUsuario(usuario);
		nuevoCanje.setRecompensa(recompensa);
		nuevoCanje.setUsado(false);
		nuevoCanje.setToken(UUID.randomUUID().toString()); 

		// 6. Guardar
		QR_Canje canjeGuardado = qrCanjeService.save(nuevoCanje);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(canjeGuardado);
	}

	// --- MÉTODO ACTUALIZADO PARA VALIDAR CON CÓDIGO STRING ---
	@PutMapping("/qr_canjeos/validar")
    public ResponseEntity<?> validarCanje(@RequestBody ValidacionRequest request) {
        
        // 1. Buscar el QR
        QR_Canje qr = qrCanjeService.findByToken(request.token);

        if (qr == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "El código QR no existe."));
        }

        // 2. Verificar si ya fue usado
        if (Boolean.TRUE.equals(qr.getUsado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
                    .body(Map.of("mensaje", "Este código ya fue utilizado."));
        }

        // 3. --- NUEVA VALIDACIÓN POR CÓDIGO STRING ---
        // Obtenemos el código del Auspiciante dueño del premio
        String codigoDueño = qr.getRecompensa().getAuspiciante().getCodigo();
        
        // Validamos (ignora mayúsculas/minúsculas para que no haya lio)
        if (codigoDueño == null || !codigoDueño.equalsIgnoreCase(request.codigo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Prohibido
                    .body(Map.of(
                        "mensaje", "Código de local incorrecto.",
                        "error", "Este premio pertenece a: " + qr.getRecompensa().getAuspiciante().getNombre()
                    ));
        }

        // 4. Todo correcto: Canjear
        qr.setUsado(true);
        qr.setFecha_usado(LocalDateTime.now());
        QR_Canje actualizado = qrCanjeService.save(qr);

        // Devolvemos más datos para que el Frontend muestre la alerta bonita
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