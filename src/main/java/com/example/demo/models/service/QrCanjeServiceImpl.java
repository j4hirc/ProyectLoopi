package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IQrCanje;
import com.example.demo.models.entity.QR_Canje;

@Service
public class QrCanjeServiceImpl implements IQrCanjeService{
	

	@Autowired
	private IQrCanje IQrCanjeDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<QR_Canje> findAll() {
		// TODO Auto-generated method stub
		return (List<QR_Canje>) IQrCanjeDao.findAll();	
	}
		
	//Guardar
	@Override
	@Transactional
	public QR_Canje save(QR_Canje QR_Canje) {
		// TODO Auto-generated method stub
		return IQrCanjeDao.save(QR_Canje);
	}

		
	@Override
	@Transactional(readOnly = true)
	public QR_Canje findById(Long id) {
		// TODO Auto-generated method stub
		return IQrCanjeDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		IQrCanjeDao.deleteById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public QR_Canje findByToken(String token) {
		return IQrCanjeDao.findByToken(token);
	}
	

}
