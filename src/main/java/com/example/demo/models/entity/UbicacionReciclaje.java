package com.example.demo.models.entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ubicacion_reciclaje")
public class UbicacionReciclaje implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_ubicacion_reciclaje;

    private String nombre;
    private String direccion;
    private Double latitud;
    private Double longitud;
    
    @Column(name = "foto", columnDefinition = "TEXT")
    private String foto;

    @ManyToOne
    @JoinColumn(name = "id_parroquia")
    private Parroquia parroquia;

    @ManyToOne
    @JoinColumn(name = "id_reciclador")
    @JsonIgnoreProperties({"ubicaciones", "solicitudes", "hibernateLazyInitializer", "handler"})
    private Usuario reciclador;

    // --- AQUÍ ESTÁ LA CLAVE DE LA EDICIÓN ---
    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties({"ubicacion", "hibernateLazyInitializer", "handler"})
    private List<UbicacionMaterial> materialesAceptados;
    
    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties("ubicacion")
    private List<HorarioReciclador> horarios;

	public Long getId_ubicacion_reciclaje() {
		return id_ubicacion_reciclaje;
	}

	public void setId_ubicacion_reciclaje(Long id_ubicacion_reciclaje) {
		this.id_ubicacion_reciclaje = id_ubicacion_reciclaje;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public Parroquia getParroquia() {
		return parroquia;
	}

	public void setParroquia(Parroquia parroquia) {
		this.parroquia = parroquia;
	}

	public Usuario getReciclador() {
		return reciclador;
	}

	public void setReciclador(Usuario reciclador) {
		this.reciclador = reciclador;
	}

	public List<UbicacionMaterial> getMaterialesAceptados() {
		return materialesAceptados;
	}

	public void setMaterialesAceptados(List<UbicacionMaterial> materialesAceptados) {
		this.materialesAceptados = materialesAceptados;
	}

	public List<HorarioReciclador> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<HorarioReciclador> horarios) {
		this.horarios = horarios;
	}

    
}