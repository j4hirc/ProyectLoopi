package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IAuspicianteDao;
import com.example.demo.models.entity.Auspiciante;



@Service
public class AuspicianteServiceImpl implements IAuspicianteService{
	

	@Autowired
	private IAuspicianteDao AuspicianteDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<Auspiciante> findAll() {
		// TODO Auto-generated method stub
		return (List<Auspiciante>) AuspicianteDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public Auspiciante save(Auspiciante auspiciante) {
		// TODO Auto-generated method stub
		return AuspicianteDao.save(auspiciante);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Auspiciante findById(Long id) {
		// TODO Auto-generated method stub
		return AuspicianteDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		AuspicianteDao.deleteById(id);
	}
	

}
