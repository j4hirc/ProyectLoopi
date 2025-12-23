package com.example.demo.models.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.models.entity.FormularioRecicladorMaterial;

public interface IFormularioRecicladorMaterialDao extends CrudRepository<FormularioRecicladorMaterial, Long>{
	
	@Query("""
		    SELECT frm
		    FROM FormularioRecicladorMaterial frm
		    JOIN FETCH frm.material
		    WHERE frm.formulario.id_formulario = :idFormulario
		""")
		List<FormularioRecicladorMaterial> findByFormulario(
		    @Param("idFormulario") Long idFormulario
		);


}
