package com.example.demo.models.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "material")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Material implements Serializable {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_material;
	
    private String nombre;
    
    @JsonProperty("tipo_material") 
    @Column(name = "tipo_material")
    private String tipo_Material;
    
    @JsonProperty("puntos_por_kg")
    @Column(name = "puntos_por_kg")
    private Integer puntosPorKg;
    private String descripcion;
    
    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String imagen;
    
    
    
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	public Long getId_material() {
		return id_material;
	}
	public void setId_material(Long id_material) {
		this.id_material = id_material;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTipo_Material() {
		return tipo_Material;
	}
	public void setTipo_Material(String tipo_Material) {
		this.tipo_Material = tipo_Material;
	}
	public Integer getPuntosPorKg() {
		return puntosPorKg;
	}
	public void setPuntosPorKg(Integer puntosPorKg) {
		this.puntosPorKg = puntosPorKg;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
    
    
}