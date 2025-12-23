package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.demo.models.DAO.IFormularioRecicladorMaterialDao;
import com.example.demo.models.entity.FormularioRecicladorMaterial;

@Service
public class IFormularioRecicladorMaterialImpl implements IFormularioRecicladorMaterialService {

	
	@Autowired
	private IFormularioRecicladorMaterialDao FormularioRecicladorMaterialDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<FormularioRecicladorMaterial> findAll() {
		// TODO Auto-generated method stub
		return (List<FormularioRecicladorMaterial>) FormularioRecicladorMaterialDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public FormularioRecicladorMaterial save(FormularioRecicladorMaterial FormularioRecicladorMaterial) {
		// TODO Auto-generated method stub
		return FormularioRecicladorMaterialDao.save(FormularioRecicladorMaterial);
	}

		
	@Override
	@Transactional(readOnly = true)
	public FormularioRecicladorMaterial findById(Long id) {
		// TODO Auto-generated method stub
		return FormularioRecicladorMaterialDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		FormularioRecicladorMaterialDao.deleteById(id);
	}

	@Override
	public List<FormularioRecicladorMaterial> findByFormulario(Long idFormulario) {
		// TODO Auto-generated method stub
		return FormularioRecicladorMaterialDao.findByFormulario(idFormulario);
	}
	
	
	
	
	
}
