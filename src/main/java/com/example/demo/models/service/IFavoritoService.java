package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Favorito;

public interface IFavoritoService {
	
	List<Favorito> findByUsuario(Long cedula);

    Favorito save(Favorito favorito);

    void eliminarPorIds(Long cedula, Long idUbicacion);
    
    public void deleteById(Long id);

}
