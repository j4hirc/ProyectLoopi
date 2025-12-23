package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Multimedia;

public interface IMultimedioService {
	
public List<Multimedia> findAll();
	
	public Multimedia save(Multimedia Multimedia);
	
	public Multimedia findById(Long id);
	
	public void delete(Long id);

}
