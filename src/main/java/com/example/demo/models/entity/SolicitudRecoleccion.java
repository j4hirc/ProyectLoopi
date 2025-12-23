package com.example.demo.models.entity;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;


@Entity
@Table(name = "solicitud_recoleccion")
public class SolicitudRecoleccion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_solicitud;

    private LocalDateTime fecha_creacion;
    private LocalDateTime fecha_recoleccion_estimada;
    private LocalDateTime fecha_recoleccion_real;
    private String estado;
    private Integer puntos_ganados;
    
    @Column(name = "foto_evidencia", columnDefinition = "TEXT")
    private String fotoEvidencia;
    
    

    @ManyToOne
    @JoinColumn(name = "id_usuario_solicitante")
    @JsonIgnoreProperties({"solicitudes", "ubicaciones", "hibernateLazyInitializer", "handler"})
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "id_reciclador")
    @JsonIgnoreProperties({"solicitudes", "ubicaciones", "hibernateLazyInitializer", "handler"})
    private Usuario reciclador;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_reciclaje")
    @JsonIgnoreProperties({"solicitudesRecibidas", "hibernateLazyInitializer", "handler"})
    private UbicacionReciclaje ubicacion;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL)
    private List<DetalleEntrega> detalles;

    @PrePersist
    public void prePersist() {
        this.fecha_creacion = LocalDateTime.now();
    }

	public Long getId_solicitud() {
		return id_solicitud;
	}

	public void setId_solicitud(Long id_solicitud) {
		this.id_solicitud = id_solicitud;
	}

	public LocalDateTime getFecha_creacion() {
		return fecha_creacion;
	}

	public void setFecha_creacion(LocalDateTime fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}

	public LocalDateTime getFecha_recoleccion_estimada() {
		return fecha_recoleccion_estimada;
	}

	public void setFecha_recoleccion_estimada(LocalDateTime fecha_recoleccion_estimada) {
		this.fecha_recoleccion_estimada = fecha_recoleccion_estimada;
	}

	public LocalDateTime getFecha_recoleccion_real() {
		return fecha_recoleccion_real;
	}

	public void setFecha_recoleccion_real(LocalDateTime fecha_recoleccion_real) {
		this.fecha_recoleccion_real = fecha_recoleccion_real;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getPuntos_ganados() {
		return puntos_ganados;
	}

	public void setPuntos_ganados(Integer puntos_ganados) {
		this.puntos_ganados = puntos_ganados;
	}

	public Usuario getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(Usuario solicitante) {
		this.solicitante = solicitante;
	}

	public Usuario getReciclador() {
		return reciclador;
	}

	public void setReciclador(Usuario reciclador) {
		this.reciclador = reciclador;
	}

	public UbicacionReciclaje getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(UbicacionReciclaje ubicacion) {
		this.ubicacion = ubicacion;
	}

	public List<DetalleEntrega> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetalleEntrega> detalles) {
		this.detalles = detalles;
	}

	public String getFotoEvidencia() {
		return fotoEvidencia;
	}

	public void setFotoEvidencia(String fotoEvidencia) {
		this.fotoEvidencia = fotoEvidencia;
	}
    
	
	
    
    
    
    
}