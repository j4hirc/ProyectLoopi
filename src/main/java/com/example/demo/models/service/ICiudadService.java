package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Ciudad;

public interface ICiudadService {
	
public List<Ciudad> findAll();
	
	public Ciudad save(Ciudad Ciudad);
	
	public Ciudad findById(Long id);
	
	public void delete(Long id);

}
