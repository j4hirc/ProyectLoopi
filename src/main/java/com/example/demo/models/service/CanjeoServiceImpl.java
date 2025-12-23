package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.demo.models.DAO.ICanjeoDao;
import com.example.demo.models.entity.Canjeo;

@Service
public class CanjeoServiceImpl implements ICanjeoService {
	
	@Autowired
	private ICanjeoDao canjeoDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<Canjeo> findAll() {
		// TODO Auto-generated method stub
		return (List<Canjeo>) canjeoDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public Canjeo save(Canjeo Canjeo) {
		// TODO Auto-generated method stub
		return canjeoDao.save(Canjeo);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Canjeo findById(Long id) {
		// TODO Auto-generated method stub
		return canjeoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		canjeoDao.deleteById(id);
	}
	

}
