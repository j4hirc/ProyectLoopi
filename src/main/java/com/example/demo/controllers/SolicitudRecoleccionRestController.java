package com.example.demo.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.example.demo.models.entity.Logro;
import com.example.demo.models.entity.Notificacion;
import com.example.demo.models.entity.Rango;
import com.example.demo.models.entity.SolicitudRecoleccion;
// IMPORTANTE: Aquí importamos tu clase correcta
import com.example.demo.models.entity.DetalleEntrega; 
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.entity.UsuarioLogro;
import com.example.demo.models.service.ILogroService;
import com.example.demo.models.service.INotificacionService;
import com.example.demo.models.service.IRangoService;
import com.example.demo.models.service.ISolicitudRecoleccionService;
import com.example.demo.models.service.IUsuarioLogroService;
import com.example.demo.models.service.IUsuarioService;
import com.example.demo.models.service.SupabaseStorageService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SolicitudRecoleccionRestController {

    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private ISolicitudRecoleccionService solicitudRecoleccionService;
    
    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private IRangoService rangoService;

    @Autowired
    private ILogroService logroService;

    @Autowired
    private IUsuarioLogroService usuarioLogroService;

    @Autowired
    private SupabaseStorageService storageService;

    // Configuración del Mapper para fechas
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @GetMapping("/solicitud_recolecciones")
    public List<SolicitudRecoleccion> indext() { return solicitudRecoleccionService.findAll(); }

    @GetMapping("/solicitud_recolecciones/{id}")
    public SolicitudRecoleccion show(@PathVariable Long id) { return solicitudRecoleccionService.findById(id); }

    // =================================================================
    // CREAR SOLICITUD (SOLUCIÓN FINAL ERROR 500)
    // =================================================================
    @PostMapping(value = "/solicitud_recolecciones", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam("datos") String datosJson, 
            @RequestParam(value = "archivo", required = false) MultipartFile archivo
    ) {
        SolicitudRecoleccion solicitudRecoleccion;
        try {
            // 1. Convertir JSON a Objeto Java
            solicitudRecoleccion = objectMapper.readValue(datosJson, SolicitudRecoleccion.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error al leer JSON: " + e.getMessage()));
        }

        try {
            // 2. Subir Foto a Supabase (si el usuario mandó una)
            if (archivo != null && !archivo.isEmpty()) {
                String urlImagen = storageService.subirImagen(archivo);
                solicitudRecoleccion.setFotoEvidencia(urlImagen); 
            }

            // 3. Vincular Usuario Solicitante (Evita errores de "usuario detached")
            if (solicitudRecoleccion.getSolicitante() != null && solicitudRecoleccion.getSolicitante().getCedula() != null) {
                 Usuario usuarioReal = usuarioService.findById(solicitudRecoleccion.getSolicitante().getCedula());
                 if (usuarioReal != null) {
                     solicitudRecoleccion.setSolicitante(usuarioReal);
                 } else {
                     return ResponseEntity.badRequest().body("Usuario solicitante no encontrado.");
                 }
            }

            // 4. Fecha por defecto
            if (solicitudRecoleccion.getFecha_creacion() == null) {
                solicitudRecoleccion.setFecha_creacion(LocalDateTime.now());
            }

            // =====================================================================
            // 5. ¡AQUÍ ESTÁ EL ARREGLO! (Relación Bidireccional)
            // =====================================================================
            // Le decimos a cada DetalleEntrega: "Tu padre es esta solicitud"
            if (solicitudRecoleccion.getDetalles() != null && !solicitudRecoleccion.getDetalles().isEmpty()) {
                for (DetalleEntrega detalle : solicitudRecoleccion.getDetalles()) {
                    detalle.setSolicitud(solicitudRecoleccion); 
                }
            }

            // 6. Guardar en BD (El CascadeType.ALL guardará los detalles automáticamente)
            SolicitudRecoleccion nueva = solicitudRecoleccionService.save(solicitudRecoleccion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);

        } catch (Exception e) {
            e.printStackTrace(); // Esto te mostrará el error exacto en la consola si falla
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error interno al guardar: " + e.getMessage()));
        }
    }

    @GetMapping("/solicitud_recolecciones/contar/{cedula}")
    public long contarRecoleccionesUsuario(@PathVariable Long cedula) {
        return solicitudRecoleccionService.contarEntregasAprobadas(cedula);
    }

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
        
        if(solicitudDetails.getFecha_recoleccion_real() != null) {
            solicitudActual.setFecha_recoleccion_real(solicitudDetails.getFecha_recoleccion_real());
        }

        if ("FINALIZADO".equals(nuevoEstado)) {

            Integer puntosGanados = solicitudDetails.getPuntos_ganados();

            if (puntosGanados == null || puntosGanados <= 0) {
                return ResponseEntity.badRequest().body("Puntos inválidos");
            }

            Usuario usuario = solicitudActual.getSolicitante();

            if (usuario == null) {
                return ResponseEntity.badRequest().body("La solicitud no tiene usuario asociado");
            }

            int puntosActuales = (usuario.getPuntos_actuales() != null) ? usuario.getPuntos_actuales() : 0;
            usuario.setPuntos_actuales(puntosActuales + puntosGanados);
            solicitudActual.setPuntos_ganados(puntosGanados);

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

            usuarioService.save(usuario); 
            validarYOtorgarLogros(usuario);
        }

        solicitudRecoleccionService.saveDirect(solicitudActual);

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

    private void validarYOtorgarLogros(Usuario usuario) {
        int misPuntos = (usuario.getPuntos_actuales() != null) ? usuario.getPuntos_actuales() : 0;
        List<Logro> todosLosLogros = logroService.findAll();
        List<UsuarioLogro> misLogros = usuarioLogroService.findByUsuarioCedula(usuario.getCedula());
        List<Long> idsMisLogros = misLogros.stream().map(ul -> ul.getLogro().getId_logro()).collect(Collectors.toList());

        for (Logro logro : todosLosLogros) {
            if (idsMisLogros.contains(logro.getId_logro())) continue;

            if (logro.getPuntos_ganados() != null && misPuntos >= logro.getPuntos_ganados()) {
                UsuarioLogro nuevo = new UsuarioLogro();
                nuevo.setUsuario(usuario);
                nuevo.setLogro(logro);
                usuarioLogroService.save(nuevo);
                crearNotificacion(usuario, "LOGRO", "¡Nueva Insignia! 🏅", "Has desbloqueado: " + logro.getNombre());
            }
        }
    }

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
                .collect(Collectors.toList());
    }
}