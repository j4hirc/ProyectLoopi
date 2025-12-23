package com.example.demo.models.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "rango")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rango implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_rango;
	
	private String nombre_rango;
	
	@Column(columnDefinition = "TEXT")
	private String imagen;
	
	
	
	

	public Long getId_rango() {
		return id_rango;
	}

	public void setId_rango(Long id_rango) {
		this.id_rango = id_rango;
	}

	public String getNombre_rango() {
		return nombre_rango;
	}

	public void setNombre_rango(String nombre_rango) {
		this.nombre_rango = nombre_rango;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	
	
	
	

}
