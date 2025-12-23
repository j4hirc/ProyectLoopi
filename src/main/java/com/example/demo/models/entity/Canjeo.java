package com.example.demo.models.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "canjeo")
public class Canjeo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_canjeo;
    
    private LocalDateTime fecha_canjeo;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_recompensa")
    private Recompensa recompensa;
    
    @PrePersist
    public void prePersist() {
        this.fecha_canjeo = LocalDateTime.now();
    }



	public Long getId_canjeo() {
		return id_canjeo;
	}



	public void setId_canjeo(Long id_canjeo) {
		this.id_canjeo = id_canjeo;
	}



	public LocalDateTime getFecha_canjeo() {
		return fecha_canjeo;
	}

	public void setFecha_canjeo(LocalDateTime fecha_canjeo) {
		this.fecha_canjeo = fecha_canjeo;
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