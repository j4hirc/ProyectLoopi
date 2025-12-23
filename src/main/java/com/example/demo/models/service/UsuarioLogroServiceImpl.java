package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IUsuarioLogroDao;
import com.example.demo.models.entity.UsuarioLogro;

@Service
public class UsuarioLogroServiceImpl implements IUsuarioLogroService {
	
	@Autowired
	private IUsuarioLogroDao usuarioLogroDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<UsuarioLogro> findAll() {
		// TODO Auto-generated method stub
		return (List<UsuarioLogro>) usuarioLogroDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public UsuarioLogro save(UsuarioLogro UsuarioLogro) {
		// TODO Auto-generated method stub
		return usuarioLogroDao.save(UsuarioLogro);
	}

		
	@Override
	@Transactional(readOnly = true)
	public UsuarioLogro findById(Long id) {
		// TODO Auto-generated method stub
		return usuarioLogroDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		usuarioLogroDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UsuarioLogro> findByUsuarioCedula(Long cedula) {
	    return usuarioLogroDao.findByUsuario_Cedula(cedula);
	}



}
