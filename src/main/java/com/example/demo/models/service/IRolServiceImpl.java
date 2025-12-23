package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.demo.models.DAO.IRolDao;
import com.example.demo.models.entity.Rol;

@Service
public class IRolServiceImpl implements IRolService {
	
	@Autowired
	private IRolDao rolDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<Rol> findAll() {
		// TODO Auto-generated method stub
		return (List<Rol>) rolDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public Rol save(Rol Rol) {
		// TODO Auto-generated method stub
		return rolDao.save(Rol);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Rol findById(Long id) {
		// TODO Auto-generated method stub
		return rolDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		rolDao.deleteById(id);
	}
}
