package com.example.demo.models.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "usuarios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(unique = true, nullable = false)
    private Long cedula; 

    private String primer_nombre;
    private String segundo_nombre;
    private String apellido_paterno;
    private String apellido_materno;
    private Character genero;
    
    
    @Column(unique = true, nullable = false)
    private String correo;
    
    private String password;
    
    @Column(name = "foto", columnDefinition = "TEXT")
    private String foto;
    private boolean estado;
    private Integer puntos_actuales;
    
    @Column(name="fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fecha_nacimiento;

    @Column(name="fecha_creacion", updatable = false)
    @Temporal(TemporalType.DATE) 
    private Date fecha_creacion;

  
    @PrePersist
    public void prePersist() {
        this.fecha_creacion = new Date(); 
        this.estado = true; 
    }
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rango")
    private Rango rango;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parroquia")
    private Parroquia parroquia;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UsuarioRol> roles;


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsuarioLogro> logros;
    
    
    
    

	public Rango getRango() {
		return rango;
	}
	
	
	


	public Character getGenero() {
		return genero;
	}





	public void setRango(Rango rango) {
		this.rango = rango;
	}


	public Parroquia getParroquia() {
		return parroquia;
	}


	public void setParroquia(Parroquia parroquia) {
		this.parroquia = parroquia;
	}


	public List<UsuarioRol> getRoles() {
		return roles;
	}


	public void setRoles(List<UsuarioRol> roles) {
		this.roles = roles;
	}


	public List<UsuarioLogro> getLogros() {
		return logros;
	}


	public void setLogros(List<UsuarioLogro> logros) {
		this.logros = logros;
	}


	public void setGenero(Character genero) {
		this.genero = genero;
	}


	public Long getCedula() {
		return cedula;
	}


	public void setCedula(Long cedula) {
		this.cedula = cedula;
	}


	public String getPrimer_nombre() {
		return primer_nombre;
	}


	public void setPrimer_nombre(String primer_nombre) {
		this.primer_nombre = primer_nombre;
	}


	public String getSegundo_nombre() {
		return segundo_nombre;
	}


	public void setSegundo_nombre(String segundo_nombre) {
		this.segundo_nombre = segundo_nombre;
	}


	public String getApellido_paterno() {
		return apellido_paterno;
	}


	public void setApellido_paterno(String apellido_paterno) {
		this.apellido_paterno = apellido_paterno;
	}


	public String getApellido_materno() {
		return apellido_materno;
	}


	public void setApellido_materno(String apellido_materno) {
		this.apellido_materno = apellido_materno;
	}



	public String getCorreo() {
		return correo;
	}


	public void setCorreo(String correo) {
		this.correo = correo;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getFoto() {
		return foto;
	}


	public void setFoto(String foto) {
		this.foto = foto;
	}


	public boolean isEstado() {
		return estado;
	}


	public void setEstado(boolean estado) {
		this.estado = estado;
	}


	public Integer getPuntos_actuales() {
		return puntos_actuales;
	}


	public void setPuntos_actuales(Integer puntos_actuales) {
		this.puntos_actuales = puntos_actuales;
	}


	public Date getFecha_nacimiento() {
		return fecha_nacimiento;
	}


	public void setFecha_nacimiento(Date fecha_nacimiento) {
		this.fecha_nacimiento = fecha_nacimiento;
	}


	public Date getFecha_creacion() {
		return fecha_creacion;
	}


	public void setFecha_creacion(Date fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}
    
	
	
    
    

}
