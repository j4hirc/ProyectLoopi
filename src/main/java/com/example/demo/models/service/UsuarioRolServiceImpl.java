package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.demo.models.DAO.IUsuarioRolDao;
import com.example.demo.models.entity.UsuarioRol;

@Service
public class UsuarioRolServiceImpl implements IUsuarioRolService {
	
	@Autowired
	private IUsuarioRolDao usuarioRolDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<UsuarioRol> findAll() {
		// TODO Auto-generated method stub
		return (List<UsuarioRol>) usuarioRolDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public UsuarioRol save(UsuarioRol UsuarioRol) {
		// TODO Auto-generated method stub
		return usuarioRolDao.save(UsuarioRol);
	}

		
	@Override
	@Transactional(readOnly = true)
	public UsuarioRol findById(Long id) {
		// TODO Auto-generated method stub
		return usuarioRolDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		usuarioRolDao.deleteById(id);
	}
	
	@Override
	public List<UsuarioRol> findByUsuarioId(Long usuarioId) {
	    return usuarioRolDao.findByUsuario_Cedula(usuarioId);
	}
	

}
