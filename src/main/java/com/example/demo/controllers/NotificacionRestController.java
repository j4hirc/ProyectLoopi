package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.entity.Notificacion;
import com.example.demo.models.service.INotificacionService;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionRestController {

    @Autowired
    private INotificacionService notificacionService;


    @GetMapping("/usuario/{cedula}")
    public ResponseEntity<List<Notificacion>> listarPorUsuario(@PathVariable Long cedula) {
        return ResponseEntity.ok(
            notificacionService.listarPorUsuario(cedula)
        );
    }


    @GetMapping("/contar/{cedula}")
    public ResponseEntity<Long> contarNoLeidas(@PathVariable Long cedula) {
        return ResponseEntity.ok(
            notificacionService.contarNoLeidas(cedula)
        );
    }


    @PutMapping("/marcar-leidas/{cedula}")
    public ResponseEntity<Void> marcarTodasLeidas(@PathVariable Long cedula) {
        notificacionService.marcarTodasLeidas(cedula);
        return ResponseEntity.ok().build();
    }
}
