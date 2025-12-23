package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.ICiudadDao;
import com.example.demo.models.entity.Ciudad;

@Service
public class CiudadServiceImpl implements ICiudadService{
	

	@Autowired
	private ICiudadDao ciudadDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<Ciudad> findAll() {
		// TODO Auto-generated method stub
		return (List<Ciudad>) ciudadDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public Ciudad save(Ciudad Ciudad) {
		// TODO Auto-generated method stub
		return ciudadDao.save(Ciudad);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Ciudad findById(Long id) {
		// TODO Auto-generated method stub
		return ciudadDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		ciudadDao.deleteById(id);
	}

}
