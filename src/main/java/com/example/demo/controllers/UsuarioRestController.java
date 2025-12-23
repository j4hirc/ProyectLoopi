package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap; // Importante para guardar tokens

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import com.example.demo.models.entity.Usuario;
import com.example.demo.models.DAO.IRolDao;
import com.example.demo.models.entity.Rol;
import com.example.demo.models.entity.UsuarioRol;
import com.example.demo.models.service.IUsuarioService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UsuarioRestController {
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IRolDao rolDao;
	
	@Autowired
    private JavaMailSender mailSender;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ALMACÉN TEMPORAL DE TOKENS (Token -> Correo)
    private static final Map<String, String> tokenStore = new ConcurrentHashMap<>();

	@GetMapping("/usuarios")
	public List<Usuario> index() {
		return usuarioService.findAll();
	}

	@GetMapping("/usuarios/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Usuario usuario = usuarioService.findById(id);
		if(usuario == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario no encontrado"));
		}
		return ResponseEntity.ok(usuario);
	}

	@PostMapping("/usuarios")
    public ResponseEntity<?> create(@RequestBody Usuario usuario) {

        if (usuarioService.existsByCedula(usuario.getCedula())) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error: La cédula ya se encuentra registrada."));
        }

        if (usuarioService.existsByCorreo(usuario.getCorreo())) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error: El correo ya está registrado."));
        }

        if (usuario.getPassword() != null) {
            String plain = usuario.getPassword().trim().replace("[", "").replace("]", "");
            usuario.setPassword(passwordEncoder.encode(plain));
        }

        // Roles... (tu lógica de roles sigue igual)
        if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
            for (UsuarioRol ur : usuario.getRoles()) {
                if (ur.getRol() != null) {
                    Long idRol = ur.getRol().getId_rol();
                    Rol rolReal = (idRol != null) ? rolDao.findById(idRol).orElse(null) : null;
                    if (rolReal != null) {
                        ur.setRol(rolReal);
                        ur.setUsuario(usuario);
                    }
                }
            }
        }
        
        // 🔥 LÓGICA HÍBRIDA: SI EL ADMIN LO MANDA ACTIVO, NO PEDIMOS CÓDIGO 🔥
        if (usuario.isEstado()) {
            // Caso 1: Creación directa (Admin dice que es activo)
            Usuario nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        
        } else {
            // Caso 2: Creación pendiente (Admin dice inactivo o es Registro normal)
            usuario.setEstado(false); // Aseguramos que sea false
            Usuario nuevoUsuario = usuarioService.save(usuario);

            String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            tokenStore.put(token, nuevoUsuario.getCorreo());

            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("juanalberto25423@gmail.com"); 
                message.setTo(nuevoUsuario.getCorreo());
                message.setSubject("Verifica tu cuenta - Loopi");
                message.setText("Tu código de verificación es: " + token);
                
                mailSender.send(message);
                
            } catch (Exception e) {
                usuarioService.delete(nuevoUsuario.getCedula()); 
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("mensaje", "No se pudo enviar el correo. Verifica que la dirección exista."));
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Cuenta creada. Requiere verificación.", "necesita_verificacion", true));
        }
    }

    @PostMapping("/usuarios/verificar")
    public ResponseEntity<?> verificarCuenta(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigo");
        String correoToken = tokenStore.get(codigo);

        if (correoToken == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Código inválido o expirado."));
        }

        Usuario usuario = usuarioService.findByCorreo(correoToken);
        if (usuario == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Usuario no encontrado."));
        }

        // ACTIVAR CUENTA
        usuario.setEstado(true);
        usuarioService.save(usuario);
        
        // Borrar token usado
        tokenStore.remove(codigo);

        return ResponseEntity.ok(Map.of("mensaje", "¡Cuenta activada con éxito! Ya puedes iniciar sesión."));
    }

	@PutMapping("/usuarios/{id}")
	public ResponseEntity<?> update(@RequestBody Usuario usuario, @PathVariable Long id) {
		Usuario usuarioActual = usuarioService.findById(id);

		if (usuarioActual == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "El usuario no existe."));
		}

		// Validamos campo por campo. Si viene null, NO lo tocamos.
		if (usuario.getPrimer_nombre() != null) usuarioActual.setPrimer_nombre(usuario.getPrimer_nombre());
		if (usuario.getSegundo_nombre() != null) usuarioActual.setSegundo_nombre(usuario.getSegundo_nombre());
		if (usuario.getApellido_paterno() != null) usuarioActual.setApellido_paterno(usuario.getApellido_paterno());
		if (usuario.getApellido_materno() != null) usuarioActual.setApellido_materno(usuario.getApellido_materno());
		if (usuario.getGenero() != null) usuarioActual.setGenero(usuario.getGenero());
		if (usuario.getCorreo() != null) usuarioActual.setCorreo(usuario.getCorreo());
		if (usuario.getFoto() != null) usuarioActual.setFoto(usuario.getFoto());
		usuarioActual.setEstado(usuario.isEstado());
		if (usuario.getFecha_nacimiento() != null) usuarioActual.setFecha_nacimiento(usuario.getFecha_nacimiento());

if (usuario.getPuntos_actuales() != null && usuario.getPuntos_actuales() != 0) {
		    usuarioActual.setPuntos_actuales(usuario.getPuntos_actuales());
		}		
		// Relaciones: Solo actualizar si se envían
		if (usuario.getParroquia() != null) usuarioActual.setParroquia(usuario.getParroquia());
		if (usuario.getRango() != null) usuarioActual.setRango(usuario.getRango());
		
		// Estado: solo si se desea cambiar explícitamente (podrías requerir lógica extra si es boolean primitivo)
		// Por ahora asumimos que si mandan el objeto completo mantiene el estado, si es parcial cuidado.
		// usuarioActual.setEstado(usuario.isEstado()); // CUIDADO: boolean por defecto es false. Mejor no tocarlo en update parcial.

		// Password
		if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
			String pwdLimpia = usuario.getPassword().trim().replace("[", "").replace("]", "");
			// Evitar doble encriptación: verificar si ya parece un hash de BCrypt ($2a$...)
			if(!pwdLimpia.startsWith("$2a$")) {
				usuarioActual.setPassword(passwordEncoder.encode(pwdLimpia));
			}
		}

		// Roles
		if (usuario.getRoles() != null) {
			if (usuarioActual.getRoles() == null) {
				usuarioActual.setRoles(new ArrayList<>());
			}

			List<Rol> rolesObjetivo = new ArrayList<>();
			for (UsuarioRol urInput : usuario.getRoles()) {
				Rol rolEncontrado = null;
				if (urInput.getRol() != null && urInput.getRol().getId_rol() != null) {
					rolEncontrado = rolDao.findById(urInput.getRol().getId_rol()).orElse(null);
				}
				if (rolEncontrado == null && urInput.getRol() != null && urInput.getRol().getNombre() != null) {
					List<Rol> todos = (List<Rol>) rolDao.findAll();
					rolEncontrado = todos.stream()
						.filter(r -> r.getNombre().equalsIgnoreCase(urInput.getRol().getNombre()))
						.findFirst().orElse(null);
				}
				if (rolEncontrado != null && !rolesObjetivo.contains(rolEncontrado)) {
					rolesObjetivo.add(rolEncontrado);
				}
			}

			List<Long> idsObjetivo = rolesObjetivo.stream().map(Rol::getId_rol).collect(Collectors.toList());
			usuarioActual.getRoles().removeIf(urActual -> 
				urActual.getRol() == null || !idsObjetivo.contains(urActual.getRol().getId_rol())
			);

			for (Rol rolTarget : rolesObjetivo) {
				boolean yaLoTiene = usuarioActual.getRoles().stream()
					.anyMatch(ur -> ur.getRol().getId_rol().equals(rolTarget.getId_rol()));
				if (!yaLoTiene) {
					UsuarioRol nuevaRelacion = new UsuarioRol();
					nuevaRelacion.setRol(rolTarget);
					nuevaRelacion.setUsuario(usuarioActual);
					usuarioActual.getRoles().add(nuevaRelacion);
				}
			}
		}

		Usuario usuarioGuardado = usuarioService.save(usuarioActual);
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);
	}

	@DeleteMapping("/usuarios/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		usuarioService.delete(id);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Usuario usuarioLogin) {
		Usuario usuarioDb = usuarioService.findByCorreo(usuarioLogin.getCorreo());
		
		if (usuarioDb == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario no encontrado"));
		}
		
		if (!usuarioDb.isEstado()) { 
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "Tu cuenta ha sido desactivada. Contacta al administrador."));
		}
		
		String pwdRaw = usuarioLogin.getPassword();
		if (pwdRaw != null) pwdRaw = pwdRaw.trim().replace("[", "").replace("]", "");

		boolean coincide = passwordEncoder.matches(pwdRaw, usuarioDb.getPassword());
		
		if (!coincide) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Credenciales incorrectas"));
		}

		return ResponseEntity.ok(usuarioDb);
	}

	@GetMapping("/usuarios/recicladores")
	public List<Usuario> obtenerRecicladores() {
		return usuarioService.findAllRecicladores();
	}
	
    // --- LÓGICA DE RECUPERACIÓN CORREGIDA ---

	@PostMapping("/recuperar-password/solicitar")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        Usuario usuario = usuarioService.findByCorreo(correo);

        if (usuario == null) {
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, recibirás un enlace."));
        }

        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        tokenStore.put(token, correo);

        // --- ENVÍO DE CORREO REAL ---
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("juanalberto25423@gmail.com"); // Debe coincidir con application.properties
            message.setTo(correo);
            message.setSubject("Código de Recuperación - Loopi");
            message.setText("Hola " + usuario.getPrimer_nombre() + ",\n\n" +
                    "Tu código para recuperar la contraseña es: " + token + "\n\n" +
                    "Si no fuiste tú, ignora este mensaje.");
            
            mailSender.send(message); 
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al enviar el correo. Intenta más tarde."));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Se ha enviado un código a tu correo."));
    }

    @PostMapping("/recuperar-password/validar") // Cambié el path a /validar para coincidir con tu JS
    public ResponseEntity<?> cambiarPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String nuevaPassword = body.get("nuevaPassword");

        // 1. Validar si el token existe en el mapa
        String correo = tokenStore.get(token);
        
        if (correo == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Código inválido o expirado."));
        }

        // 2. Buscar usuario
        Usuario usuario = usuarioService.findByCorreo(correo);
        if (usuario == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Usuario no encontrado."));
        }

        // 3. Actualizar contraseña
        String pwdLimpia = nuevaPassword.trim().replace("[", "").replace("]", "");
        usuario.setPassword(passwordEncoder.encode(pwdLimpia));
        usuarioService.save(usuario);
        
        // 4. Eliminar token usado (para que no se pueda reusar)
        tokenStore.remove(token);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente."));
    }
    
    
    @GetMapping("/healthz")
    public String healthCheck() {
        return "OK";
    }
}