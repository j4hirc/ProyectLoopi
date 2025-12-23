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
    public Long idAuspiciante; 
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
		
		// 1. Validar que vengan los IDs necesarios
		if (datosEntrada.getUsuario() == null || datosEntrada.getUsuario().getCedula() == null ||
			datosEntrada.getRecompensa() == null || datosEntrada.getRecompensa().getId_recompensa() == null) {
			return ResponseEntity.badRequest().body(Map.of("mensaje", "Faltan datos del usuario o recompensa"));
		}

		// 2. Buscar entidades reales en BD
		Usuario usuario = usuarioService.findById(datosEntrada.getUsuario().getCedula());
		Recompensa recompensa = recompensaService.findById(datosEntrada.getRecompensa().getId_recompensa());

		if (usuario == null || recompensa == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario o Recompensa no encontrados"));
		}

		// 3. VERIFICAR PUNTOS (Lógica de Negocio)
		if (usuario.getPuntos_actuales() < recompensa.getCostoPuntos()) {
			return ResponseEntity.badRequest().body(Map.of("mensaje", "No tienes puntos suficientes para este canje"));
		}

		// 4. DESCONTAR PUNTOS Y GUARDAR USUARIO
		usuario.setPuntos_actuales(usuario.getPuntos_actuales() - recompensa.getCostoPuntos());
		usuarioService.save(usuario);

		// 5. GENERAR EL QR (TOKEN)
		QR_Canje nuevoCanje = new QR_Canje();
		nuevoCanje.setUsuario(usuario);
		nuevoCanje.setRecompensa(recompensa);
		nuevoCanje.setUsado(false); // Nace sin usar
		// Generamos un código único aleatorio (Ej: "550e8400-e29b-...")
		nuevoCanje.setToken(UUID.randomUUID().toString()); 

		// 6. GUARDAR Y RETORNAR
		QR_Canje canjeGuardado = qrCanjeService.save(nuevoCanje);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(canjeGuardado);
	}

	// Método para validar/usar el QR (Para la App del Administrador/Local)
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
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "Este código ya fue utilizado."));
        }

        // 3. --- NUEVA VALIDACIÓN DE SEGURIDAD ---
        // Verificar que el Auspiciante que escanea sea el dueño de la recompensa
        Long idDueñoRecompensa = qr.getRecompensa().getAuspiciante().getId_auspiciante();
        
        if (!idDueñoRecompensa.equals(request.idAuspiciante)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Prohibido
                    .body(Map.of(
                        "mensaje", "No puedes validar este cupón.",
                        "error", "Este premio pertenece a otro local (" + qr.getRecompensa().getAuspiciante().getNombre() + ")"
                    ));
        }

        // 4. Todo correcto: Canjear
        qr.setUsado(true);
        qr.setFecha_usado(LocalDateTime.now());
        QR_Canje actualizado = qrCanjeService.save(qr);

        return ResponseEntity.ok(Map.of(
            "mensaje", "Canje exitoso",
            "recompensa_titulo", actualizado.getRecompensa().getNombre(),
            "usuario_nombre", actualizado.getUsuario().getPrimer_nombre()
        ));
    }

	@DeleteMapping("/qr_canjeos/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		qrCanjeService.delete(id);
	}
}