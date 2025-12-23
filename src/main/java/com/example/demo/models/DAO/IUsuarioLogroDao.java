package com.example.demo.models.DAO;


import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.models.entity.UsuarioLogro;

public interface IUsuarioLogroDao extends CrudRepository<UsuarioLogro, Long>{
	
	List<UsuarioLogro> findByUsuario_Cedula(Long cedula);
}
