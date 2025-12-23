package com.example.demo.models.service;

import java.util.List;

import com.example.demo.models.entity.QR_Canje;

public interface IQrCanjeService {
	
public List<QR_Canje> findAll();
	
	public QR_Canje save(QR_Canje QR_Canje);
	
	public QR_Canje findById(Long id);
	
	public void delete(Long id);

	public QR_Canje findByToken(String token);
	
	
}
