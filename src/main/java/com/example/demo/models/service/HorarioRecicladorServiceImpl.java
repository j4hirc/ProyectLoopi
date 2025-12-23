package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.demo.models.DAO.IHorarioRecicladorDao;
import com.example.demo.models.entity.HorarioReciclador;

@Service
public class HorarioRecicladorServiceImpl implements IHorarioRecicladorService{
	
	@Autowired
	private IHorarioRecicladorDao HorarioRecicladorDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<HorarioReciclador> findAll() {
		// TODO Auto-generated method stub
		return (List<HorarioReciclador>) HorarioRecicladorDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public HorarioReciclador save(HorarioReciclador HorarioReciclador) {
		// TODO Auto-generated method stub
		return HorarioRecicladorDao.save(HorarioReciclador);
	}

		
	@Override
	@Transactional(readOnly = true)
	public HorarioReciclador findById(Long id) {
		// TODO Auto-generated method stub
		return HorarioRecicladorDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		HorarioRecicladorDao.deleteById(id);
	}

}
