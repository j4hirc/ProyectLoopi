package com.example.demo.models.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IUsuarioDao;
import com.example.demo.models.entity.Usuario;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	@Autowired
	private IUsuarioDao usuarioDao; 

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> findAll() {
		return (List<Usuario>) usuarioDao.findAll();
	}

	@Override
	@Transactional
	public Usuario save(Usuario usuario) {

		
		return usuarioDao.save(usuario);
	}

	@Override
	@Transactional(readOnly = true)
	public Usuario findById(Long id) {
		return usuarioDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		usuarioDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Usuario findByCorreo(String correo) {
		return usuarioDao.findByCorreo(correo);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByCedula(Long cedula) {
		return usuarioDao.existsByCedula(cedula);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByCorreo(String correo) {
		return usuarioDao.existsByCorreo(correo);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> findAllRecicladores() {
		return usuarioDao.findRecicladores();
	}
}