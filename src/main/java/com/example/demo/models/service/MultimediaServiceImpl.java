package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IMultimediaDao;
import com.example.demo.models.entity.Multimedia;

@Service
public class MultimediaServiceImpl implements IMultimedioService{
	

	@Autowired
	private IMultimediaDao IMultimediaDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<Multimedia> findAll() {
		// TODO Auto-generated method stub
		return (List<Multimedia>) IMultimediaDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public Multimedia save(Multimedia Multimedia) {
		// TODO Auto-generated method stub
		return IMultimediaDao.save(Multimedia);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Multimedia findById(Long id) {
		// TODO Auto-generated method stub
		return IMultimediaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		IMultimediaDao.deleteById(id);
	}

}
