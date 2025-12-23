package com.example.demo.models.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.entity.SolicitudRecoleccion;

public interface ISolicitudRecoleccionDao extends CrudRepository<SolicitudRecoleccion, Long>{
	
	@Query("select s from SolicitudRecoleccion s where s.reciclador is null and s.estado = 'VERIFICACION_ADMIN'")
    public List<SolicitudRecoleccion> findSolicitudesPendientesDeAdmin();
	
	
	@Query("SELECT COUNT(s) FROM SolicitudRecoleccion s WHERE s.solicitante.cedula = ?1 AND s.estado = 'FINALIZADO'")
    long contarEntregasAprobadas(Long cedula);
	
	@Query("""
			   SELECT s FROM SolicitudRecoleccion s
			   WHERE s.reciclador.cedula = ?1
			   ORDER BY s.fecha_creacion DESC
			""")
			List<SolicitudRecoleccion> findByRecicladorCedula(Long cedula);
	
	@Query("""
			  SELECT s
			  FROM SolicitudRecoleccion s
			  WHERE 
			    (s.reciclador IS NULL AND s.estado = 'PENDIENTE_RECOLECCION')
			    OR
			    (s.reciclador.cedula = :cedula)
			  ORDER BY s.fecha_creacion DESC
			""")
			List<SolicitudRecoleccion> solicitudesParaReciclador(@Param("cedula") Long cedula);

	
}
