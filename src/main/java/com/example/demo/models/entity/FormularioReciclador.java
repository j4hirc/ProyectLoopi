package com.example.demo.models.entity;

import java.io.Serializable;
import java.util.Date;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "formulario_reciclador")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class FormularioReciclador implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_formulario;

    private Integer anios_experiencia;

    @Column(name = "nombre_sitio")
    private String nombre_sitio;

    private Double latitud;
    private Double longitud;
    private String ubicacion;

    @Column(columnDefinition = "TEXT")
    private String foto_perfil_profesional;

    @Column(columnDefinition = "TEXT")
    private String evidencia_experiencia;

    private Boolean aprobado;
    private String observacion_admin;
    private Date fecha_solicitud;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({
        "roles",
        "logros",
        "parroquia",
        "rango"
    })
    private Usuario usuario;


    @OneToMany(mappedBy = "formulario", fetch = FetchType.LAZY, cascade = CascadeType.ALL) 
    @JsonIgnoreProperties("formulario")
    private List<HorarioReciclador> horarios;

    @OneToMany(mappedBy = "formulario", fetch = FetchType.LAZY, cascade = CascadeType.ALL) 
    @JsonIgnoreProperties("formulario")
    private List<FormularioRecicladorMaterial> materiales;

    @PrePersist
    public void prePersist() {
        this.fecha_solicitud = new Date();
    }

	public Long getId_formulario() {
		return id_formulario;
	}

	public void setId_formulario(Long id_formulario) {
		this.id_formulario = id_formulario;
	}

	public Integer getAnios_experiencia() {
		return anios_experiencia;
	}

	public void setAnios_experiencia(Integer anios_experiencia) {
		this.anios_experiencia = anios_experiencia;
	}


	public String getNombre_sitio() {
		return nombre_sitio;
	}

	public void setNombre_sitio(String nombre_sitio) {
		this.nombre_sitio = nombre_sitio;
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

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getFoto_perfil_profesional() {
		return foto_perfil_profesional;
	}

	public void setFoto_perfil_profesional(String foto_perfil_profesional) {
		this.foto_perfil_profesional = foto_perfil_profesional;
	}

	public String getEvidencia_experiencia() {
		return evidencia_experiencia;
	}

	public void setEvidencia_experiencia(String evidencia_experiencia) {
		this.evidencia_experiencia = evidencia_experiencia;
	}

	public Boolean getAprobado() {
		return aprobado;
	}

	public void setAprobado(Boolean aprobado) {
		this.aprobado = aprobado;
	}

	public String getObservacion_admin() {
		return observacion_admin;
	}

	public void setObservacion_admin(String observacion_admin) {
		this.observacion_admin = observacion_admin;
	}

	public Date getFecha_solicitud() {
		return fecha_solicitud;
	}

	public void setFecha_solicitud(Date fecha_solicitud) {
		this.fecha_solicitud = fecha_solicitud;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<HorarioReciclador> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<HorarioReciclador> horarios) {
		this.horarios = horarios;
	}

	public List<FormularioRecicladorMaterial> getMateriales() {
		return materiales;
	}

	public void setMateriales(List<FormularioRecicladorMaterial> materiales) {
		this.materiales = materiales;
	}

    
}
