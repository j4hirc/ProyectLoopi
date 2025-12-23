package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Material;

public interface IMaterialService {
	
public List<Material> findAll();
	
	public Material save(Material Material);
	
	public Material findById(Long id);
	
	public void delete(Long id);

}
