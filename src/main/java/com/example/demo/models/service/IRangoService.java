package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Rango;

public interface IRangoService {
	
public List<Rango> findAll();
	
	public Rango save(Rango Rango);
	
	public Rango findById(Long id);
	
	public void delete(Long id);

}
