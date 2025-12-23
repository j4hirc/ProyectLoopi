package com.example.demo.models.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "parroquia")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Parroquia implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_parroquia;
	
	private String nombre_parroquia;
	
	
	
	@ManyToOne
    @JoinColumn(name = "id_ciudad")
    private Ciudad ciudad;
	
	
	

	public Ciudad getCiudad() {
		return ciudad;
	}

	public void setCiudad(Ciudad ciudad) {
		this.ciudad = ciudad;
	}

	public Long getId_parroquia() {
		return id_parroquia;
	}

	public void setId_parroquia(Long id_parroquia) {
		this.id_parroquia = id_parroquia;
	}

	public String getNombre_parroquia() {
		return nombre_parroquia;
	}

	public void setNombre_parroquia(String nombre_parroquia) {
		this.nombre_parroquia = nombre_parroquia;
	} 
	
	
	

}
