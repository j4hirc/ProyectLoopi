package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Canjeo;

public interface ICanjeoService {
	
public List<Canjeo> findAll();
	
	public Canjeo save(Canjeo Canjeo);
	
	public Canjeo findById(Long id);
	
	public void delete(Long id);

}
