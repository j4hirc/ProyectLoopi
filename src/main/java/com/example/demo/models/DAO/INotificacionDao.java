package com.example.demo.models.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.entity.Notificacion;

public interface INotificacionDao extends CrudRepository<Notificacion, Long> {

    // 🔔 Todas las notificaciones de un usuario (ordenadas por fecha)
    @Query("""
        SELECT n 
        FROM Notificacion n
        WHERE n.usuario.cedula = :cedula
        ORDER BY n.fecha_creacion DESC
    """)
    List<Notificacion> listarPorUsuario(@Param("cedula") Long cedula);

    // 🔴 Notificaciones NO leídas
    @Query("""
        SELECT n 
        FROM Notificacion n
        WHERE n.usuario.cedula = :cedula
          AND n.leido = false
        ORDER BY n.fecha_creacion DESC
    """)
    List<Notificacion> listarNoLeidas(@Param("cedula") Long cedula);

    // 🔢 Contador de no leídas (para la campanita)
    @Query("""
        SELECT COUNT(n)
        FROM Notificacion n
        WHERE n.usuario.cedula = :cedula
          AND n.leido = false
    """)
    Long contarNoLeidas(@Param("cedula") Long cedula);

    // ✔️ Marcar todas como leídas
    @Modifying
    @Transactional
    @Query("""
        UPDATE Notificacion n
        SET n.leido = true
        WHERE n.usuario.cedula = :cedula
    """)
    void marcarTodasLeidas(@Param("cedula") Long cedula);
}
