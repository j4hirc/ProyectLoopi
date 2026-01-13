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
		return (List<Auspiciante>) AuspicianteDao.findAll();	
	}
		
	@Override
	@Transactional
	public Auspiciante save(Auspiciante auspiciante) {
		return AuspicianteDao.save(auspiciante);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Auspiciante findById(Long id) {
		return AuspicianteDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		AuspicianteDao.deleteById(id);
	}
	

}
