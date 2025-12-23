package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.demo.models.DAO.IMaterialDao;
import com.example.demo.models.entity.Material;

@Service
public class MaterialServiceImpl implements IMaterialService{
	

	@Autowired
	private IMaterialDao materialDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<Material> findAll() {
		// TODO Auto-generated method stub
		return (List<Material>) materialDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public Material save(Material Material) {
		// TODO Auto-generated method stub
		return materialDao.save(Material);
	}

		
	@Override
	@Transactional(readOnly = true)
	public Material findById(Long id) {
		// TODO Auto-generated method stub
		return materialDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		materialDao.deleteById(id);
	}

}
