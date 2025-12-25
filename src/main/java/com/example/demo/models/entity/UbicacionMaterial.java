package com.example.demo.models.entity;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ubicacion_material")
public class UbicacionMaterial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_ubicacion_material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion_reciclaje")
    @JsonIgnoreProperties({"materialesAceptados", "horarios", "hibernateLazyInitializer", "handler"})
    private UbicacionReciclaje ubicacion;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "id_material")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Material material;

	public Long getId_ubicacion_material() {
		return id_ubicacion_material;
	}

	public void setId_ubicacion_material(Long id_ubicacion_material) {
		this.id_ubicacion_material = id_ubicacion_material;
	}

	public UbicacionReciclaje getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(UbicacionReciclaje ubicacion) {
		this.ubicacion = ubicacion;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
    
    
    
    
}