package com.example.demo.models.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.models.entity.Notificacion;

public interface INotificacionService {
	
	List<Notificacion> listarPorUsuario(Long cedula);
	
	List<Notificacion> listarNoLeidas(Long cedula);
	
	Long contarNoLeidas(Long cedula);
	
	void marcarTodasLeidas(Long cedula);
	
	Notificacion save(Notificacion notificacion);
	
	Optional<Notificacion> findById(Long id);
	
	

}
