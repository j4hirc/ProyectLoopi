package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IParroquiaDao;
import com.example.demo.models.entity.Parroquia;

@Service
public class ParroquiaServiceImpl implements IParroquiService {
	

	@Autowired
	private IParroquiaDao parroquiaDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<Parroquia> findAll() {
		// TODO Auto-generated method stub
		return (List<Parroquia>) parroquiaDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public Parroquia save(Parroquia Parroquia) {
		// TODO Auto-generated method stub
		return parroquiaDao.save(Parroquia);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Parroquia findById(Long id) {
		// TODO Auto-generated method stub
		return parroquiaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		parroquiaDao.deleteById(id);
	}

}
