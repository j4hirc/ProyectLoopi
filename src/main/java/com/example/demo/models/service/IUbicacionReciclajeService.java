package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.UbicacionReciclaje;

public interface IUbicacionReciclajeService {
	
public List<UbicacionReciclaje> findAll();
	
	public UbicacionReciclaje save(UbicacionReciclaje UbicacionReciclaje);
	
	public UbicacionReciclaje findById(Long id);
	
	public void delete(Long id);

}
