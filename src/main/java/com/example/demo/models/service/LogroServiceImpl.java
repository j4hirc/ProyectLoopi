package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.demo.models.DAO.ILogroDao;
import com.example.demo.models.entity.Logro;

@Service
public class LogroServiceImpl implements ILogroService {
	
	@Autowired
	private ILogroDao logroDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<Logro> findAll() {
		// TODO Auto-generated method stub
		return (List<Logro>) logroDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public Logro save(Logro Logro) {
		// TODO Auto-generated method stub
		return logroDao.save(Logro);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Logro findById(Long id) {
		// TODO Auto-generated method stub
		return logroDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		logroDao.deleteById(id);
	}

}
