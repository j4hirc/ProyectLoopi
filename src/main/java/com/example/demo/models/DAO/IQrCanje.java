package com.example.demo.models.DAO;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.models.entity.QR_Canje;

public interface IQrCanje extends CrudRepository<QR_Canje, Long>{
	
	public QR_Canje findByToken(String token);
}
