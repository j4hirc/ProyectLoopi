package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IFavoritoDao;
import com.example.demo.models.entity.Favorito;


@Service
public class FavoritoServiceImpl implements IFavoritoService {

    @Autowired
    private IFavoritoDao favoritoDao;

    @Override
    @Transactional(readOnly = true)
    public List<Favorito> findByUsuario(Long cedula) {
        return favoritoDao.listarPorUsuario(cedula);
    }

    @Override
    @Transactional
    public Favorito save(Favorito favorito) {

        Long cedula = favorito.getUsuario().getCedula();
        Long idUbicacion = favorito.getUbicacion().getId_ubicacion_reciclaje();

        favoritoDao.buscarFavorito(cedula, idUbicacion)
            .ifPresent(f -> {
                throw new RuntimeException("Ya es favorito");
            });

        return favoritoDao.save(favorito);
    }

    @Override
    @Transactional
    public void eliminarPorIds(Long cedula, Long idUbicacion) {

        Favorito favorito = favoritoDao.buscarFavorito(cedula, idUbicacion)
            .orElseThrow(() -> new RuntimeException("Favorito no existe"));

        favoritoDao.delete(favorito);
    }

	@Override
	public void deleteById(Long id) {
		favoritoDao.deleteById(id);
		
	}
}
