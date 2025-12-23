package com.example.demo.models.service;

import java.util.List;


import com.example.demo.models.entity.Auspiciante;

public interface IAuspicianteService {
	
	public List<Auspiciante> findAll();
	
	public Auspiciante save(Auspiciante auspiciante);
	
	public Auspiciante findById(Long id);
	
	public void delete(Long id);

}
