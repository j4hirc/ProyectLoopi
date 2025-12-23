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
@Table(name = "auspiciante")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Auspiciante implements Serializable {
   
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_auspiciante;
    
    private String nombre;
    private String descripcion;
    
    @Column(columnDefinition = "TEXT")
    private String imagen;
    
    private String codigo;

    
    
    
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Long getId_auspiciante() {
		return id_auspiciante;
	}
	public void setId_auspiciante(Long id_auspiciante) {
		this.id_auspiciante = id_auspiciante;
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
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

    
}
