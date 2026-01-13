package com.example.demo.models.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "multimedia")
public class Multimedia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_multimedia;


    @Column(columnDefinition = "TEXT")
    private String imagenes;

    @Column(columnDefinition = "TEXT")
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;


    public Long getId_multimedia() {
        return id_multimedia;
    }

    public void setId_multimedia(Long id_multimedia) {
        this.id_multimedia = id_multimedia;
    }


    public String getImagenes() {
        return imagenes;
    }

    public void setImagenes(String imagenes) {
        this.imagenes = imagenes;
    }
    

    public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    
}
