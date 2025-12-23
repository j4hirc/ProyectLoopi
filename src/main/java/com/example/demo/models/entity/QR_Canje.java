package com.example.demo.models.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "qr_canje")
public class QR_Canje implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_QrCanje;
	
	@Column(nullable = false, unique = true)
	private String token;
	
	private LocalDateTime fecha_generado;
	
	private LocalDateTime fecha_usado;
	
	private Boolean usado;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	
	@ManyToOne
	@JoinColumn(name = "id_recompensa")
	private Recompensa recompensa;
	
	
	@PrePersist
	public void prePersist() {
		this.fecha_generado = LocalDateTime.now();
	}
	
	
	

	public Boolean getUsado() {
		return usado;
	}




	public void setUsado(Boolean usado) {
		this.usado = usado;
	}




	public Long getId_QrCanje() {
		return id_QrCanje;
	}


	public void setId_QrCanje(Long id_QrCanje) {
		this.id_QrCanje = id_QrCanje;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public LocalDateTime getFecha_generado() {
		return fecha_generado;
	}




	public void setFecha_generado(LocalDateTime fecha_generado) {
		this.fecha_generado = fecha_generado;
	}




	public LocalDateTime getFecha_usado() {
		return fecha_usado;
	}




	public void setFecha_usado(LocalDateTime fecha_usado) {
		this.fecha_usado = fecha_usado;
	}




	public Boolean getEstado() {
		return usado;
	}


	public void setEstado(Boolean estado) {
		this.usado = estado;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}


	public Recompensa getRecompensa() {
		return recompensa;
	}


	public void setRecompensa(Recompensa recompensa) {
		this.recompensa = recompensa;
	}
	

}
