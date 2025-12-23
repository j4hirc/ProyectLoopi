package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IDetalleEntregaDao;
import com.example.demo.models.entity.DetalleEntrega;

@Service
public class DetalleEntregaServiceImpl implements IDetalleEntregaService {
	
	@Autowired
	private IDetalleEntregaDao detalleEntregaDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<DetalleEntrega> findAll() {
		// TODO Auto-generated method stub
		return (List<DetalleEntrega>) detalleEntregaDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public DetalleEntrega save(DetalleEntrega DetalleEntrega) {
		// TODO Auto-generated method stub
		return detalleEntregaDao.save(DetalleEntrega);
	}

		
	@Override
	@Transactional(readOnly = true)
	public DetalleEntrega findById(Long id) {
		// TODO Auto-generated method stub
		return detalleEntregaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		detalleEntregaDao.deleteById(id);
	}

}
