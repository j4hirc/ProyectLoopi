package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.DetalleEntrega;

public interface IDetalleEntregaService {
	
public List<DetalleEntrega> findAll();
	
	public DetalleEntrega save(DetalleEntrega detalleEntrega);
	
	public DetalleEntrega findById(Long id);
	
	public void delete(Long id);

}
