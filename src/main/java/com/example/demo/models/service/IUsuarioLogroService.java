package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.UsuarioLogro;

public interface IUsuarioLogroService {
	
public List<UsuarioLogro> findAll();
	
	public UsuarioLogro save(UsuarioLogro UsuarioLogro);
	
	public UsuarioLogro findById(Long id);
	
	public void delete(Long id);
	
	List<UsuarioLogro> findByUsuarioCedula(Long cedula);

}
