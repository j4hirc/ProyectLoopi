package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.HorarioReciclador;

public interface IHorarioRecicladorService {
	
public List<HorarioReciclador> findAll();
	
	public HorarioReciclador save(HorarioReciclador HorarioReciclador);
	
	public HorarioReciclador findById(Long id);
	
	public void delete(Long id);

}
