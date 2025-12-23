package com.example.demo.models.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "usuario_logro")
public class UsuarioLogro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario_logro;
    


    @Column(name="fecha_creacion")
    @Temporal(TemporalType.DATE)
    private Date fecha_obtenida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_logro")
    private Logro logro;
    
    @PrePersist
    public void prePersist() {
        this.fecha_obtenida = new Date(); 
    }

	public Long getId_usuario_logro() {
		return id_usuario_logro;
	}

	public void setId_usuario_logro(Long id_usuario_logro) {
		this.id_usuario_logro = id_usuario_logro;
	}

	public Date getFecha_obtenida() {
		return fecha_obtenida;
	}

	public void setFecha_obtenida(Date fecha_obtenida) {
		this.fecha_obtenida = fecha_obtenida;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Logro getLogro() {
		return logro;
	}

	public void setLogro(Logro logro) {
		this.logro = logro;
	}
    
    
    
    
    
}