package com.example.demo.models.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.INotificacionDao;
import com.example.demo.models.entity.Notificacion;

@Service
public class NotificacionServiceImpl implements INotificacionService{

	@Autowired
	private INotificacionDao notificacionDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Notificacion> listarPorUsuario(Long cedula) {
		return notificacionDao.listarPorUsuario(cedula);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Notificacion> listarNoLeidas(Long cedula) {
		// TODO Auto-generated method stub
		return notificacionDao.listarNoLeidas(cedula);
	}

	@Override
	@Transactional(readOnly = true)
	public Long contarNoLeidas(Long cedula) {
		// TODO Auto-generated method stub
		return notificacionDao.contarNoLeidas(cedula);
	}

	@Override
	@Transactional
	public void marcarTodasLeidas(Long cedula) {
		notificacionDao.marcarTodasLeidas(cedula);
		
	}

	@Override
	@Transactional
	public Notificacion save(Notificacion notificacion) {
		// TODO Auto-generated method stub
		return notificacionDao.save(notificacion);
	}

	@Override
	@Transactional
	public Optional<Notificacion> findById(Long id) {
		// TODO Auto-generated method stub
		return notificacionDao.findById(id);
	}
	
	

}
