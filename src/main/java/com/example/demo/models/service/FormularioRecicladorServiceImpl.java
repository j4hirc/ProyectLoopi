package com.example.demo.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.DAO.IFormularioRecicladorDao;
import com.example.demo.models.DAO.IRolDao;
import com.example.demo.models.DAO.IUbicacionReciclajeDao;
import com.example.demo.models.DAO.IUsuarioDao;
import com.example.demo.models.DAO.IUsuarioRolDao;
import com.example.demo.models.entity.FormularioReciclador;
import com.example.demo.models.entity.FormularioRecicladorMaterial;
import com.example.demo.models.entity.HorarioReciclador;
import com.example.demo.models.entity.Rol;
import com.example.demo.models.entity.UbicacionReciclaje;
import com.example.demo.models.entity.Usuario;
import com.example.demo.models.entity.UsuarioRol; 

@Service
public class FormularioRecicladorServiceImpl implements IFormularioRecicladorService {
	
	@Autowired
	private IFormularioRecicladorDao formularioRecicladorDao;
	


	@Autowired
	private IUbicacionReciclajeDao ubicacionReciclajeDao;
	
	@Autowired
	private IUsuarioRolDao usuarioRolDao;
	
	@Autowired
	private IRolDao rolDao;
	
	@Autowired
	private IUsuarioDao usuarioDao;
	
	@Override
	@Transactional(readOnly = true )
	public List<FormularioReciclador> findAll() {
		return (List<FormularioReciclador>) formularioRecicladorDao.findAll();	
	}
		
	@Override
	@Transactional
	public FormularioReciclador save(FormularioReciclador formulario) {
	    

	    if(formulario.getHorarios() != null) {
	        System.out.println("Guardando formulario con " + formulario.getHorarios().size() + " horarios.");
	        for (HorarioReciclador h : formulario.getHorarios()) {
	            h.setFormulario(formulario); // Esto enlaza el ID
	        }
	    } else {
	        System.out.println("ATENCIÓN: La lista de horarios es NULL");
	    }

	    if (formulario.getMateriales() != null) {
	        for (FormularioRecicladorMaterial m : formulario.getMateriales()) {
	            m.setFormulario(formulario);
	        }
	    }

	    return formularioRecicladorDao.save(formulario);
	}
	
	@Override
	@Transactional(readOnly = true)
	public FormularioReciclador findById(Long id) {
		return formularioRecicladorDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		formularioRecicladorDao.deleteById(id);
	}
	
	@Override
	@Transactional
	public void aprobarFormulario(Long id, String observacion) { // <--- Nuevo parámetro

	    FormularioReciclador form = formularioRecicladorDao.findById(id).orElse(null);

	    if (form == null) throw new RuntimeException("Formulario no existe");
	    if (Boolean.TRUE.equals(form.getAprobado())) return;

	    Usuario usuario = usuarioDao.findById(form.getUsuario().getCedula())
	            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	    form.setAprobado(true);
	    // AQUÍ USAMOS EL MENSAJE DEL ADMIN O UNO POR DEFECTO
	    form.setObservacion_admin(observacion != null && !observacion.isEmpty() ? observacion : "Solicitud Aprobada");
	    
	    formularioRecicladorDao.save(form);

	    // ... (El resto de tu lógica de UbicacionReciclaje y Roles se queda IGUAL) ...
	    UbicacionReciclaje u = new UbicacionReciclaje();
	    u.setNombre(form.getNombre_sitio());
	    u.setDireccion(form.getUbicacion());
	    u.setLatitud(form.getLatitud());
	    u.setLongitud(form.getLongitud());
	    u.setReciclador(usuario);
	    u.setParroquia(usuario.getParroquia());
	    ubicacionReciclajeDao.save(u);

	    Rol rolReciclador = rolDao.findById(2L).orElseThrow(() -> new RuntimeException("Rol ID 2 no existe"));
	    boolean yaTiene = usuarioRolDao.existeRolParaUsuario(usuario.getCedula(), rolReciclador.getId_rol());

	    if (!yaTiene) {
	        UsuarioRol ur = new UsuarioRol();
	        ur.setUsuario(usuario);
	        ur.setRol(rolReciclador);
	        usuarioRolDao.save(ur);
	    }
	}



}