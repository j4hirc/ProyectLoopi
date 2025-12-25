package com.example.demo.models.entity;

import java.io.Serializable;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "horario_reciclador")
public class HorarioReciclador implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_horario;

	private String dia_semana;
	@JsonFormat(pattern = "HH:mm:ss") 
    private LocalTime hora_inicio;

    @JsonFormat(pattern = "HH:mm:ss") 
    private LocalTime hora_fin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_formulario")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private FormularioReciclador formulario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ubicacion_reciclaje")
	@JsonIgnore 
	private UbicacionReciclaje ubicacion;

	
	
	
	
	
	public UbicacionReciclaje getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(UbicacionReciclaje ubicacion) {
		this.ubicacion = ubicacion;
	}

	public Long getId_horario() {
		return id_horario;
	}

	public void setId_horario(Long id_horario) {
		this.id_horario = id_horario;
	}

	public String getDia_semana() {
		return dia_semana;
	}

	public void setDia_semana(String dia_semana) {
		this.dia_semana = dia_semana;
	}

	public LocalTime getHora_inicio() {
		return hora_inicio;
	}

	public void setHora_inicio(LocalTime hora_inicio) {
		this.hora_inicio = hora_inicio;
	}

	public LocalTime getHora_fin() {
		return hora_fin;
	}

	public void setHora_fin(LocalTime hora_fin) {
		this.hora_fin = hora_fin;
	}

	public FormularioReciclador getFormulario() {
		return formulario;
	}

	public void setFormulario(FormularioReciclador formulario) {
		this.formulario = formulario;
	}

}