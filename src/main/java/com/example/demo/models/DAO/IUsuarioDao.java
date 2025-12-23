package com.example.demo.models.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.models.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{
	
	public Usuario findByCorreo(String correo);
	boolean existsByCedula(Long cedula);
    boolean existsByCorreo(String correo);
    
    @Query("select u from Usuario u join u.roles ur where ur.rol.id_rol = 2")
    List<Usuario> findRecicladores();

}
 