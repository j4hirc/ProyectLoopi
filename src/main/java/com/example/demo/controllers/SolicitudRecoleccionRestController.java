package com.example.demo.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.entity.Logro;
import com.example.demo.models.entity.Notificacion;
import com.example.demo.models.entity.Rango;
import com.example.demo.models.entity.SolicitudRecoleccion;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.entity.UsuarioLogro;
import com.example.demo.models.service.ILogroService;
import com.example.demo.models.service.INotificacionService;
import com.example.demo.models.service.IRangoService;
import com.example.demo.models.service.ISolicitudRecoleccionService;
import com.example.demo.models.service.IUsuarioLogroService;
import com.example.demo.models.service.UsuarioServiceImpl;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SolicitudRecoleccionRestController {

    private final UsuarioServiceImpl usuarioServiceImpl;
    
    @Autowired
    private ISolicitudRecoleccionService solicitudRecoleccionService;
    
    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private IRangoService rangoService;

    // --- 1. INYECTAMOS SERVICIOS DE LOGROS ---
    @Autowired
    private ILogroService logroService;

    @Autowired
    private IUsuarioLogroService usuarioLogroService;

    SolicitudRecoleccionRestController(UsuarioServiceImpl usuarioServiceImpl) {
        this.usuarioServiceImpl = usuarioServiceImpl;
    }

    // ... (Tus métodos GET y POST siguen igual) ...
    @GetMapping("/solicitud_recolecciones")
    public List<SolicitudRecoleccion> indext() { return solicitudRecoleccionService.findAll(); }

    @GetMapping("/solicitud_recolecciones/{id}")
    public SolicitudRecoleccion show(@PathVariable Long id) { return solicitudRecoleccionService.findById(id); }

    @PostMapping("/solicitud_recolecciones")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudRecoleccion create(@RequestBody SolicitudRecoleccion solicitudRecoleccion) {
        return solicitudRecoleccionService.save(solicitudRecoleccion);
    }

    @GetMapping("/solicitud_recolecciones/contar/{cedula}")
    public long contarRecoleccionesUsuario(@PathVariable Long cedula) {
        return solicitudRecoleccionService.contarEntregasAprobadas(cedula);
    }

    // --- AQUÍ ESTÁ LA MAGIA ---
    @PutMapping("/solicitud_recolecciones/{id}")
    @Transactional
    public ResponseEntity<?> update(@RequestBody SolicitudRecoleccion solicitudDetails, @PathVariable Long id) {

        SolicitudRecoleccion solicitudActual = solicitudRecoleccionService.findById(id);

        if (solicitudActual == null) {
            return ResponseEntity.notFound().build();
        }

        String estadoAnterior = solicitudActual.getEstado();
        String nuevoEstado = solicitudDetails.getEstado();

        solicitudActual.setEstado(nuevoEstado);
        solicitudActual.setFecha_recoleccion_real(solicitudDetails.getFecha_recoleccion_real());

        if ("FINALIZADO".equals(nuevoEstado)) {

            Integer puntosGanados = solicitudDetails.getPuntos_ganados();

            if (puntosGanados == null || puntosGanados <= 0) {
                return ResponseEntity.badRequest().body("Puntos inválidos");
            }

            Usuario usuario = solicitudActual.getSolicitante();

            if (usuario == null) {
                return ResponseEntity.badRequest().body("La solicitud no tiene usuario asociado");
            }

            // Sumar puntos de forma segura
            int puntosActuales = (usuario.getPuntos_actuales() != null) ? usuario.getPuntos_actuales() : 0;
            usuario.setPuntos_actuales(puntosActuales + puntosGanados);
            solicitudActual.setPuntos_ganados(puntosGanados);

            // --- LÓGICA DE RANGO (La que ya tenías) ---
            long historialAprobado = solicitudRecoleccionService.contarEntregasAprobadas(usuario.getCedula());
            long totalRecolecciones = historialAprobado + 1; 
            long idRangoCalculado = (totalRecolecciones / 25) + 1;
            Long idRangoActual = (usuario.getRango() != null) ? usuario.getRango().getId_rango() : 0L;

            if (idRangoCalculado > idRangoActual) {
                Rango nuevoRango = rangoService.findById(idRangoCalculado);
                if (nuevoRango != null) {
                    usuario.setRango(nuevoRango);
                    crearNotificacion(usuario, "LOGRO", "¡Subiste de Nivel! 🏆", "Felicidades, ahora eres rango: " + nuevoRango.getNombre_rango());
                }
            }

            usuarioServiceImpl.save(usuario);
            
            // --- 2. VALIDACIÓN AUTOMÁTICA DE LOGROS ---
            validarYOtorgarLogros(usuario);
        }

        solicitudRecoleccionService.saveDirect(solicitudActual);

        // Notificaciones de estado
        if (estadoAnterior == null || !estadoAnterior.equals(nuevoEstado)) {
            Usuario destinatario = solicitudActual.getSolicitante();
            if (destinatario != null) {
                String titulo = "Estado actualizado";
                String mensaje = "El estado cambió a: " + nuevoEstado;
                
                if("ACEPTADA".equals(nuevoEstado)) { titulo = "Solicitud aceptada 🚛"; mensaje = "Tu solicitud fue aceptada."; }
                else if("FINALIZADO".equals(nuevoEstado)) { titulo = "Recolección finalizada ✅"; mensaje = "Ganaste " + solicitudActual.getPuntos_ganados() + " puntos."; }
                else if("RECHAZADO".equals(nuevoEstado)) { titulo = "Solicitud rechazada ❌"; mensaje = "Tu solicitud no pudo procesarse."; }
                
                crearNotificacion(destinatario, "SOLICITUD", titulo, mensaje);
            }
        }

        return ResponseEntity.ok(solicitudActual);
    }

    // --- MÉTODO PRIVADO PARA VALIDAR LOGROS DINÁMICAMENTE ---
    private void validarYOtorgarLogros(Usuario usuario) {
        // 1. Obtenemos puntos actuales
        int misPuntos = (usuario.getPuntos_actuales() != null) ? usuario.getPuntos_actuales() : 0;

        // 2. Traemos TODOS los logros de la BD (Lo que creaste en el CRUD)
        List<Logro> todosLosLogros = logroService.findAll();

        // 3. Traemos los que YA TIENE el usuario
        List<UsuarioLogro> misLogros = usuarioLogroService.findByUsuarioCedula(usuario.getCedula());
        List<Long> idsMisLogros = misLogros.stream().map(ul -> ul.getLogro().getId_logro()).collect(Collectors.toList());

        // 4. Comparamos
        for (Logro logro : todosLosLogros) {
            // Si ya lo tiene, pasamos al siguiente
            if (idsMisLogros.contains(logro.getId_logro())) continue;

            // REGLA: Si mis puntos >= puntos del logro, ME LO GANO
            // (Asumiendo que 'puntos_ganados' en la entidad Logro es la meta requerida)
            if (logro.getPuntos_ganados() != null && misPuntos >= logro.getPuntos_ganados()) {
                
                UsuarioLogro nuevo = new UsuarioLogro();
                nuevo.setUsuario(usuario);
                nuevo.setLogro(logro);
                // La fecha se pone sola en @PrePersist
                usuarioLogroService.save(nuevo);

                // Notificación al usuario
                crearNotificacion(usuario, "LOGRO", "¡Nueva Insignia! 🏅", "Has desbloqueado: " + logro.getNombre());
            }
        }
    }

    // Método auxiliar para notificaciones
    private void crearNotificacion(Usuario usuario, String tipo, String titulo, String mensaje) {
        Notificacion noti = new Notificacion();
        noti.setUsuario(usuario);
        noti.setFecha_creacion(LocalDateTime.now());
        noti.setLeido(false);
        noti.setTipo(tipo);
        noti.setTitulo(titulo);
        noti.setMensaje(mensaje);
        noti.setEntidad_referencia(tipo.equals("LOGRO") ? "LOGRO" : "SOLICITUD_RECOLECCION");
        notificacionService.save(noti);
    }

    // ... (Tus métodos Delete y Get auxiliares siguen igual) ...
    @DeleteMapping("/solicitud_recolecciones/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { solicitudRecoleccionService.delete(id); }
    
    @GetMapping("/solicitud_recolecciones/pendientes")
    public List<SolicitudRecoleccion> listarPendientes() { return solicitudRecoleccionService.findSolicitudesPendientesDeAdmin(); }
    
    @GetMapping("/solicitud_recolecciones/reciclador/{cedula}")
    public List<SolicitudRecoleccion> listarPorReciclador(@PathVariable Long cedula) { return solicitudRecoleccionService.findByRecicladorCedula(cedula); }
    
    @GetMapping("/solicitud_recolecciones/usuario/{cedula}")
    public List<SolicitudRecoleccion> listarPorUsuario(@PathVariable Long cedula) {
        return solicitudRecoleccionService.findAll().stream()
                .filter(s -> s.getSolicitante() != null && s.getSolicitante().getCedula().equals(cedula))
                .toList();
    }
}