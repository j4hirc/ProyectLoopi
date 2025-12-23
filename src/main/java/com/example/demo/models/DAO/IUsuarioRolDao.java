package com.example.demo.models.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.entity.UsuarioRol;

public interface IUsuarioRolDao extends CrudRepository<UsuarioRol, Long> {
	
	List<UsuarioRol> findByUsuario_Cedula(Long cedula);
	
	@Query("""
	        SELECT COUNT(ur) > 0
	        FROM UsuarioRol ur
	        WHERE ur.usuario.cedula = :cedula
	          AND ur.rol.id_rol = :idRol
	    """)
	    boolean existeRolParaUsuario(
	        @Param("cedula") Long cedula,
	        @Param("idRol") Long idRol
	    );
}

