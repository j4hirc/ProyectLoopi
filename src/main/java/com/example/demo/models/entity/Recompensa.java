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
@Table(name = "recompensa")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Recompensa implements Serializable {
   
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_recompensa;
    private String nombre;
    private String descripcion;
    private Integer costoPuntos;
    
    private String direccion;
    private Double latitud;
    private Double longitud;

    @ManyToOne
    @JoinColumn(name = "id_auspiciante")
    private Auspiciante auspiciante;

	public Long getId_recompensa() {
		return id_recompensa;
	}

	public void setId_recompensa(Long id_recompensa) {
		this.id_recompensa = id_recompensa;
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

	public Integer getCostoPuntos() {
		return costoPuntos;
	}

	public void setCostoPuntos(Integer costoPuntos) {
		this.costoPuntos = costoPuntos;
	}

	public Auspiciante getAuspiciante() {
		return auspiciante;
	}

	public void setAuspiciante(Auspiciante auspiciante) {
		this.auspiciante = auspiciante;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public Double getLatitud() {
		return latitud;
	}

	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	public Double getLongitud() {
		return longitud;
	}

	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}
	
	
    
    
   
}