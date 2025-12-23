package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Parroquia;

public interface IParroquiService {
	
	
public List<Parroquia> findAll();
	
	public Parroquia save(Parroquia Parroquia);
	
	public Parroquia findById(Long id);
	
	public void delete(Long id);

}
