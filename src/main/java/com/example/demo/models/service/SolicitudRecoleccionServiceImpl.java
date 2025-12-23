package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.ISolicitudRecoleccionDao;
import com.example.demo.models.DAO.IUbicacionReciclajeDao; 
import com.example.demo.models.entity.DetalleEntrega; // <--- NO OLVIDES IMPORTAR ESTO
import com.example.demo.models.entity.SolicitudRecoleccion;
import com.example.demo.models.entity.UbicacionReciclaje;

@Service
public class SolicitudRecoleccionServiceImpl implements ISolicitudRecoleccionService {
	
	@Autowired
	private ISolicitudRecoleccionDao solicitudRecoleccionDao;
	
	@Autowired
	private IUbicacionReciclajeDao ubicacionDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<SolicitudRecoleccion> findAll() {
		return (List<SolicitudRecoleccion>) solicitudRecoleccionDao.findAll();	
	}
		

	@Override
	@Transactional
	public SolicitudRecoleccion save(SolicitudRecoleccion solicitud) {
		
	
	    if (solicitud.getUbicacion() == null || solicitud.getUbicacion().getId_ubicacion_reciclaje() == null) {
	        throw new RuntimeException("Error: La solicitud debe tener una ubicación válida.");
	    }
	
	   
	    UbicacionReciclaje puntoFijo = ubicacionDao.findById(solicitud.getUbicacion().getId_ubicacion_reciclaje())
	            .orElseThrow(() -> new RuntimeException("Ubicación no encontrada en base de datos"));

	    
	
	    if (puntoFijo.getReciclador() == null) {
	        // CASO A: PUNTO SIN ENCARGADO
	        
	     
	        if (solicitud.getFotoEvidencia() == null || solicitud.getFotoEvidencia().trim().isEmpty()) {
	            throw new RuntimeException("Para puntos sin encargado, la FOTO DE EVIDENCIA es obligatoria.");
	        }

	        solicitud.setReciclador(null); 
	        solicitud.setEstado("VERIFICACION_ADMIN");
	        solicitud.setUbicacion(puntoFijo);

	    } else {
	        
	        solicitud.setReciclador(puntoFijo.getReciclador());
	        solicitud.setEstado("PENDIENTE_RECOLECCION"); 	        	    
	        solicitud.setUbicacion(puntoFijo);
	    }

    
        if (solicitud.getDetalles() != null && !solicitud.getDetalles().isEmpty()) {
            for (DetalleEntrega detalle : solicitud.getDetalles()) {
                detalle.setSolicitud(solicitud); 
            }
        }


	    return solicitudRecoleccionDao.save(solicitud);
	}
	

	@Override
	@Transactional(readOnly = true)
	public SolicitudRecoleccion findById(Long id) {
		return solicitudRecoleccionDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		solicitudRecoleccionDao.deleteById(id);
	}
	
	@Override
    @Transactional(readOnly = true)
    public List<SolicitudRecoleccion> findSolicitudesPendientesDeAdmin() {
        return solicitudRecoleccionDao.findSolicitudesPendientesDeAdmin();
    }
	
	@Override
	@Transactional
	public SolicitudRecoleccion saveDirect(SolicitudRecoleccion solicitud) {
	    return solicitudRecoleccionDao.save(solicitud);
	}
	
	@Override
    @Transactional(readOnly = true)
    public long contarEntregasAprobadas(Long cedula) {
        return solicitudRecoleccionDao.contarEntregasAprobadas(cedula);
    }


	@Override
	@Transactional(readOnly = true)
	public List<SolicitudRecoleccion> findByRecicladorCedula(Long cedula) {
		return solicitudRecoleccionDao.findByRecicladorCedula(cedula);
	}

}