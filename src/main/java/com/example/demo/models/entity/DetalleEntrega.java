package com.example.demo.models.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "detalle_entrega")
public class DetalleEntrega implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_detalle;

    private Double cantidad_kg;
    private String calidad;
    private Double valor_estimado;
    private Integer puntos_calculados;

    @ManyToOne
    @JoinColumn(name = "id_solicitud")
    @JsonIgnore
    private SolicitudRecoleccion solicitud;

    @ManyToOne
    @JoinColumn(name = "id_material")
    private Material material;

	public Long getId_detalle() {
		return id_detalle;
	}

	public void setId_detalle(Long id_detalle) {
		this.id_detalle = id_detalle;
	}

	public Double getCantidad_kg() {
		return cantidad_kg;
	}

	public void setCantidad_kg(Double cantidad_kg) {
		this.cantidad_kg = cantidad_kg;
	}

	public String getCalidad() {
		return calidad;
	}

	public void setCalidad(String calidad) {
		this.calidad = calidad;
	}

	public Double getValor_estimado() {
		return valor_estimado;
	}

	public void setValor_estimado(Double valor_estimado) {
		this.valor_estimado = valor_estimado;
	}

	public Integer getPuntos_calculados() {
		return puntos_calculados;
	}

	public void setPuntos_calculados(Integer puntos_calculados) {
		this.puntos_calculados = puntos_calculados;
	}

	public SolicitudRecoleccion getSolicitud() {
		return solicitud;
	}

	public void setSolicitud(SolicitudRecoleccion solicitud) {
		this.solicitud = solicitud;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
    
    
    
    
}
