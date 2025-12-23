package com.example.demo.models.DAO;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.entity.Favorito;

public interface IFavoritoDao extends CrudRepository<Favorito, Long>{
	
    @Query("SELECT f FROM Favorito f WHERE f.usuario.cedula = :cedula AND f.ubicacion.id_ubicacion_reciclaje = :idUbicacion")
    Optional<Favorito> buscarFavorito(@Param("cedula") Long cedula, @Param("idUbicacion") Long idUbicacion);

    @Query("SELECT f FROM Favorito f WHERE f.usuario.cedula = :cedula")
    List<Favorito> listarPorUsuario(@Param("cedula") Long cedula);
}
