package com.example.demo.models.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "formulario_reciclador_material")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class FormularioRecicladorMaterial implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_formulario_material;

   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formulario")
    private FormularioReciclador formulario;

 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_material")
    private Material material;


	public Long getId_formulario_material() {
		return id_formulario_material;
	}


	public void setId_formulario_material(Long id_formulario_material) {
		this.id_formulario_material = id_formulario_material;
	}


	public FormularioReciclador getFormulario() {
		return formulario;
	}


	public void setFormulario(FormularioReciclador formulario) {
		this.formulario = formulario;
	}


	public Material getMaterial() {
		return material;
	}


	public void setMaterial(Material material) {
		this.material = material;
	}


}