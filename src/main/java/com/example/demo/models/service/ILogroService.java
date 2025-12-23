package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.Logro;

public interface ILogroService {
	
public List<Logro> findAll();
	
	public Logro save(Logro Logro);
	
	public Logro findById(Long id);
	
	public void delete(Long id);

}
