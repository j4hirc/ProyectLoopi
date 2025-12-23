package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.FormularioRecicladorMaterial;

public interface IFormularioRecicladorMaterialService {

	public List<FormularioRecicladorMaterial> findAll();

	public FormularioRecicladorMaterial save(FormularioRecicladorMaterial FormularioRecicladorMaterial);

	public FormularioRecicladorMaterial findById(Long id);

	public void delete(Long id);

	List<FormularioRecicladorMaterial> findByFormulario(Long idFormulario);
}
