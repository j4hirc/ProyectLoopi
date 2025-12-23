package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.SolicitudRecoleccion;

public interface ISolicitudRecoleccionService {
	
public List<SolicitudRecoleccion> findAll();
	
	public SolicitudRecoleccion save(SolicitudRecoleccion SolicitudRecoleccion);
	
	public SolicitudRecoleccion findById(Long id);
	
	public void delete(Long id);
	
	public List<SolicitudRecoleccion> findSolicitudesPendientesDeAdmin();
	
	SolicitudRecoleccion saveDirect(SolicitudRecoleccion solicitud);

	long contarEntregasAprobadas(Long cedula);
	
	List<SolicitudRecoleccion> findByRecicladorCedula(Long cedula);

	

}
