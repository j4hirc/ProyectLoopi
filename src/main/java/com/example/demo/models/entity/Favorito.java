package com.example.demo.models.entity;

import jakarta.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "favorito")
public class Favorito implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_favorito;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion_reciclaje")
    private UbicacionReciclaje ubicacion;

	public Long getId_favorito() {
		return id_favorito;
	}

	public void setId_favorito(Long id_favorito) {
		this.id_favorito = id_favorito;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public UbicacionReciclaje getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(UbicacionReciclaje ubicacion) {
		this.ubicacion = ubicacion;
	}
    
    
    
    
    
    
}