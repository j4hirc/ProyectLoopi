package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Rol;

public interface IRolService {
	
public List<Rol> findAll();
	
	public Rol save(Rol Rol);
	
	public Rol findById(Long id);
	
	public void delete(Long id);

}
