package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.FormularioReciclador;

public interface IFormularioRecicladorService {
	
public List<FormularioReciclador> findAll();
	
	public FormularioReciclador save(FormularioReciclador formularioReciclador);
	
	public FormularioReciclador findById(Long id);
	
	public void delete(Long id);
	
	public void aprobarFormulario(Long id, String observacion);

}
