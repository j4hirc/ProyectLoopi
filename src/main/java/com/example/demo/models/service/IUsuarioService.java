package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Usuario;

public interface IUsuarioService {
	
public List<Usuario> findAll();
	
	public Usuario save(Usuario Usuario);
	
	public Usuario findById(Long id);
	
	public void delete(Long id);
	
	public Usuario findByCorreo(String correo);
	
	public boolean existsByCedula(Long cedula);
	
    public boolean existsByCorreo(String correo);
    
    public List<Usuario> findAllRecicladores();

}
