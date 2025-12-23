package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Recompensa;

public interface IRecompensaService {
	
public List<Recompensa> findAll();
	
	public Recompensa save(Recompensa Recompensa);
	
	public Recompensa findById(Long id);
	
	public void delete(Long id);

}
