package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IRecompensaDao;
import com.example.demo.models.entity.Recompensa;


@Service
public class RecompensaServiceImpl implements IRecompensaService{
	

	@Autowired
	private IRecompensaDao IRecompensaDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<Recompensa> findAll() {
		// TODO Auto-generated method stub
		return (List<Recompensa>) IRecompensaDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public Recompensa save(Recompensa Recompensa) {
		// TODO Auto-generated method stub
		return IRecompensaDao.save(Recompensa);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Recompensa findById(Long id) {
		// TODO Auto-generated method stub
		return IRecompensaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		IRecompensaDao.deleteById(id);
	}

}
