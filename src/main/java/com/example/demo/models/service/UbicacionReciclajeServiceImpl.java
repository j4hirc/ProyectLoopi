package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IUbicacionReciclajeDao;
import com.example.demo.models.entity.UbicacionMaterial;
import com.example.demo.models.entity.UbicacionReciclaje; 

@Service
public class UbicacionReciclajeServiceImpl implements IUbicacionReciclajeService{
	

	@Autowired
	private IUbicacionReciclajeDao IUbicacionReciclajeDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<UbicacionReciclaje> findAll() {
		// TODO Auto-generated method stub
		return (List<UbicacionReciclaje>) IUbicacionReciclajeDao.findAll();	
	}
		
	@Override
	@Transactional
	public UbicacionReciclaje save(UbicacionReciclaje ubicacion) {

		
		if (ubicacion.getId_ubicacion_reciclaje() != null) {
			
			
			UbicacionReciclaje existente = IUbicacionReciclajeDao.findById(ubicacion.getId_ubicacion_reciclaje()).orElse(null);

			if (existente != null) {
				existente.setNombre(ubicacion.getNombre());
				existente.setDireccion(ubicacion.getDireccion());
				existente.setLatitud(ubicacion.getLatitud());
				existente.setLongitud(ubicacion.getLongitud());
				existente.setFoto(ubicacion.getFoto());
				existente.setParroquia(ubicacion.getParroquia());
				existente.setReciclador(ubicacion.getReciclador());

				if (ubicacion.getMaterialesAceptados() != null) {
					for (UbicacionMaterial um : ubicacion.getMaterialesAceptados()) {
						um.setUbicacion(existente); 
					}
				}

				existente.getMaterialesAceptados().clear(); 
				existente.getMaterialesAceptados().addAll(ubicacion.getMaterialesAceptados());

				return IUbicacionReciclajeDao.save(existente);
			}
		}


		if (ubicacion.getMaterialesAceptados() != null) {
			for (UbicacionMaterial um : ubicacion.getMaterialesAceptados()) {
				um.setUbicacion(ubicacion);
			}
		}
		
		return IUbicacionReciclajeDao.save(ubicacion);
	}
		
	@Override
	@Transactional(readOnly = true)
	public UbicacionReciclaje findById(Long id) {
		// TODO Auto-generated method stub
		return IUbicacionReciclajeDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		IUbicacionReciclajeDao.deleteById(id);
	}

}
