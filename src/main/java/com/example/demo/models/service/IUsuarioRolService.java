package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.UsuarioRol;

public interface IUsuarioRolService{
	
	
public List<UsuarioRol> findAll();
	
	public UsuarioRol save(UsuarioRol UsuarioRol);
	
	public UsuarioRol findById(Long id);
	
	public void delete(Long id);
	
	List<UsuarioRol> findByUsuarioId(Long usuarioId);


}
