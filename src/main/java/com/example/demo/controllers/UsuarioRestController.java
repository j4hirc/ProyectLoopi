package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // IMPORTANTE PARA ARCHIVOS

// IMPORTANTE PARA CONVERTIR TEXTO A JSON
import com.fasterxml.jackson.databind.ObjectMapper; 

import com.example.demo.models.entity.Usuario;
import com.example.demo.models.DAO.IRolDao;
import com.example.demo.models.DAO.IUsuarioRolDao;
import com.example.demo.models.entity.Rol;
import com.example.demo.models.entity.UsuarioRol;
import com.example.demo.models.service.IUsuarioService;
import com.example.demo.models.service.SupabaseStorageService; // TU SERVICIO DE NUBE

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UsuarioRestController {
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IRolDao rolDao;
	
	@Autowired
	private IUsuarioRolDao usuarioRolDao;
	
	@Autowired
    private JavaMailSender mailSender;
	
	@Autowired
    private SupabaseStorageService storageService;

	private ObjectMapper objectMapper = new ObjectMapper();
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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


	@PostMapping(value = "/usuarios", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> create(
            @RequestParam("datos") String usuarioJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
	    Usuario usuario;
        try {
            usuario = objectMapper.readValue(usuarioJson, Usuario.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error procesando JSON: " + e.getMessage()));
        }

	    if (usuarioService.existsByCedula(usuario.getCedula())) {
	        return ResponseEntity.badRequest().body(Map.of("mensaje", "Error: La cédula ya se encuentra registrada."));
	    }

	    if (usuarioService.existsByCorreo(usuario.getCorreo())) {
	        return ResponseEntity.badRequest().body(Map.of("mensaje", "Error: El correo ya está registrado."));
	    }

	    String email = usuario.getCorreo();
	    String dominio = email.substring(email.indexOf("@") + 1);
	    if (!tieneRegistrosMX(dominio)) {
	        return ResponseEntity.badRequest().body(Map.of("mensaje", "Error: El dominio @" + dominio + " no es válido."));
	    }

	    if (usuario.getPassword() != null) {
	        String plain = usuario.getPassword().trim().replace("[", "").replace("]", "");
	        usuario.setPassword(passwordEncoder.encode(plain));
	    }

        if (archivo != null && !archivo.isEmpty()) {
            String urlFoto = storageService.subirImagen(archivo);
            usuario.setFoto(urlFoto);
        }

        procesarRolesCreate(usuario);
	    
	    if (usuario.isEstado()) {
	        Usuario nuevoUsuario = usuarioService.save(usuario);
	        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
	    
	    } else {
	        usuario.setEstado(false); 
	        Usuario nuevoUsuario = usuarioService.save(usuario);

	        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
	        tokenStore.put(token, nuevoUsuario.getCorreo());

	        try {
                enviarCorreoVerificacion(nuevoUsuario, token);
	            return ResponseEntity.status(HttpStatus.CREATED)
	                    .body(Map.of("mensaje", "Cuenta creada. Revisa tu correo.", "necesita_verificacion", true));

	        } catch (Exception e) {
	            System.err.println("Error enviando correo: " + e.getMessage());
	            usuarioService.delete(nuevoUsuario.getCedula());
	            tokenStore.remove(token);
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of("mensaje", "Error crítico: El correo no existe. Registro cancelado."));
	        }
	    }
	}
	

	@PutMapping(value = "/usuarios/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> update(
            @PathVariable Long id, 
            @RequestParam("datos") String usuarioJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        Usuario usuarioInput;
        try {
            usuarioInput = objectMapper.readValue(usuarioJson, Usuario.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error JSON: " + e.getMessage()));
        }

		Usuario usuarioActual = usuarioService.findById(id);
		if (usuarioActual == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "El usuario no existe."));
		}

        if (archivo != null && !archivo.isEmpty()) {
            String urlFoto = storageService.subirImagen(archivo);
            usuarioActual.setFoto(urlFoto);
        }

		if (usuarioInput.getPrimer_nombre() != null) usuarioActual.setPrimer_nombre(usuarioInput.getPrimer_nombre());
		if (usuarioInput.getSegundo_nombre() != null) usuarioActual.setSegundo_nombre(usuarioInput.getSegundo_nombre());
		if (usuarioInput.getApellido_paterno() != null) usuarioActual.setApellido_paterno(usuarioInput.getApellido_paterno());
		if (usuarioInput.getApellido_materno() != null) usuarioActual.setApellido_materno(usuarioInput.getApellido_materno());
		if (usuarioInput.getGenero() != null) usuarioActual.setGenero(usuarioInput.getGenero());
		if (usuarioInput.getCorreo() != null) usuarioActual.setCorreo(usuarioInput.getCorreo());
		
		
        usuarioActual.setEstado(usuarioInput.isEstado());
		if (usuarioInput.getFecha_nacimiento() != null) usuarioActual.setFecha_nacimiento(usuarioInput.getFecha_nacimiento());

        if (usuarioInput.getPuntos_actuales() != null) {
		    usuarioActual.setPuntos_actuales(usuarioInput.getPuntos_actuales());
		}		
		
		if (usuarioInput.getParroquia() != null) usuarioActual.setParroquia(usuarioInput.getParroquia());
		if (usuarioInput.getRango() != null) usuarioActual.setRango(usuarioInput.getRango());
		
		// Password
		if (usuarioInput.getPassword() != null && !usuarioInput.getPassword().isEmpty()) {
			String pwdLimpia = usuarioInput.getPassword().trim().replace("[", "").replace("]", "");
			if(!pwdLimpia.startsWith("$2a$")) {
				usuarioActual.setPassword(passwordEncoder.encode(pwdLimpia));
			}
		}

		// 3. Actualizar Roles (Lógica extraída a método auxiliar)
		if (usuarioInput.getRoles() != null) {
            procesarRolesUpdate(usuarioActual, usuarioInput.getRoles());
		}

		Usuario usuarioGuardado = usuarioService.save(usuarioActual);
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);
	}


    private void procesarRolesCreate(Usuario usuario) {
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
    }

    private void procesarRolesUpdate(Usuario actual, List<UsuarioRol> nuevosRoles) {
        if (actual.getRoles() == null) {
            actual.setRoles(new ArrayList<>());
        }

        List<Rol> rolesObjetivo = new ArrayList<>();
        for (UsuarioRol urInput : nuevosRoles) {
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
        actual.getRoles().removeIf(urActual -> 
            urActual.getRol() == null || !idsObjetivo.contains(urActual.getRol().getId_rol())
        );

        for (Rol rolTarget : rolesObjetivo) {
            boolean yaLoTiene = actual.getRoles().stream()
                .anyMatch(ur -> ur.getRol().getId_rol().equals(rolTarget.getId_rol()));
            if (!yaLoTiene) {
                UsuarioRol nuevaRelacion = new UsuarioRol();
                nuevaRelacion.setRol(rolTarget);
                nuevaRelacion.setUsuario(actual);
                actual.getRoles().add(nuevaRelacion);
            }
        }
    }

	private boolean tieneRegistrosMX(String dominio) {
	    try {
	        Hashtable<String, String> env = new Hashtable<>();
	        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
	        DirContext ictx = new InitialDirContext(env);   
	        Attributes attrs = ictx.getAttributes(dominio, new String[] { "MX" });
	        Attribute attr = attrs.get("MX");
	        return (attr != null && attr.size() > 0);
	    } catch (Exception e) {
	        return false;
	    }
	}
    
    private void enviarCorreoVerificacion(Usuario usuario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("juanalberto25423@gmail.com"); 
        message.setTo(usuario.getCorreo());
        message.setSubject("Verifica tu cuenta - Loopi");
        message.setText("Hola " + usuario.getPrimer_nombre() + ",\n\n" +
                        "Tu código de verificación es: " + token);
        mailSender.send(message);
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

        usuario.setEstado(true);
        usuarioService.save(usuario);
        tokenStore.remove(codigo);

        return ResponseEntity.ok(Map.of("mensaje", "¡Cuenta activada con éxito!"));
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
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "Tu cuenta ha sido desactivada."));
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
	
	@PostMapping("/recuperar-password/solicitar")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        Usuario usuario = usuarioService.findByCorreo(correo);

        if (usuario == null) {
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, recibirás un enlace."));
        }

        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        tokenStore.put(token, correo);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("juanalberto25423@gmail.com"); 
            message.setTo(correo);
            message.setSubject("Código de Recuperación - Loopi");
            message.setText("Hola " + usuario.getPrimer_nombre() + ",\n\n" +
                    "Tu código para recuperar la contraseña es: " + token);
            
            mailSender.send(message); 
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al enviar el correo."));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Se ha enviado un código a tu correo."));
    }

    @PostMapping("/recuperar-password/validar") 
    public ResponseEntity<?> cambiarPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String nuevaPassword = body.get("nuevaPassword");

        String correo = tokenStore.get(token);
        if (correo == null) return ResponseEntity.badRequest().body(Map.of("mensaje", "Código inválido o expirado."));

        Usuario usuario = usuarioService.findByCorreo(correo);
        if (usuario == null) return ResponseEntity.badRequest().body(Map.of("mensaje", "Usuario no encontrado."));

        String pwdLimpia = nuevaPassword.trim().replace("[", "").replace("]", "");
        usuario.setPassword(passwordEncoder.encode(pwdLimpia));
        usuarioService.save(usuario);
        tokenStore.remove(token);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente."));
    }
    
    @GetMapping("/healthz")
    public String healthCheck() {
        return "OK";
    }
}