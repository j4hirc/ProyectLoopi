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
@Table(name = "logro")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Logro implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_logro;
    private String nombre;
    private String descripcion;
    private Integer Puntos_ganados;
    
    @Column(columnDefinition = "TEXT")
    private String imagen_logro;

    

	public Integer getPuntos_ganados() {
		return Puntos_ganados;
	}

	public void setPuntos_ganados(Integer puntos_ganados) {
		Puntos_ganados = puntos_ganados;
	}

	public String getImagen_logro() {
		return imagen_logro;
	}

	public void setImagen_logro(String imagen_logro) {
		this.imagen_logro = imagen_logro;
	}

	public Long getId_logro() {
		return id_logro;
	}

	public void setId_logro(Long id_logro) {
		this.id_logro = id_logro;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

    
    
   
}