package com.example.demo.models.entity;


import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "notificacion")
public class Notificacion implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_notificacion;
	
	private String titulo;
    private String mensaje;
    private String tipo;
    private Boolean leido;
	
    
    private LocalDateTime fecha_creacion;

    private String entidad_referencia;
    private Long id_referencia;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @PrePersist
    public void prePersist() {
        this.fecha_creacion = LocalDateTime.now();
        if (this.leido == null) {
            this.leido = false;
        }
    }


	public Long getId_notificacion() {
		return id_notificacion;
	}

	public void setId_notificacion(Long id_notificacion) {
		this.id_notificacion = id_notificacion;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Boolean getLeido() {
		return leido;
	}

	public void setLeido(Boolean leido) {
		this.leido = leido;
	}

	
	
	
	public LocalDateTime getFecha_creacion() {
		return fecha_creacion;
	}

	public void setFecha_creacion(LocalDateTime fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}

	public String getEntidad_referencia() {
		return entidad_referencia;
	}

	public void setEntidad_referencia(String entidad_referencia) {
		this.entidad_referencia = entidad_referencia;
	}

	public Long getId_referencia() {
		return id_referencia;
	}

	public void setId_referencia(Long id_referencia) {
		this.id_referencia = id_referencia;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
    
    
    
}
