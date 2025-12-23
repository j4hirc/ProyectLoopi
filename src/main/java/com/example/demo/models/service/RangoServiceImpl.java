package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IRangoDao;
import com.example.demo.models.entity.Rango;

@Service
public class RangoServiceImpl implements IRangoService {

	@Autowired
	private IRangoDao IRangoDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<Rango> findAll() {
		// TODO Auto-generated method stub
		return (List<Rango>) IRangoDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public Rango save(Rango Rango) {
		// TODO Auto-generated method stub
		return IRangoDao.save(Rango);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Rango findById(Long id) {
		// TODO Auto-generated method stub
		return IRangoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		IRangoDao.deleteById(id);
	}

}
