package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import com.example.demo.models.entity.DetalleEntrega;
import com.example.demo.models.service.IDetalleEntregaService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DetalleEntregaRestController {
	
	@Autowired
	private IDetalleEntregaService detalleEntregaService;

	@GetMapping("/detalle_entregas")
	public List<DetalleEntrega> indext() {
		return detalleEntregaService.findAll();
	}

	@GetMapping("/detalle_entregas/{id}")
	public DetalleEntrega show(@PathVariable Long id) {
		return detalleEntregaService.findById(id);
	}

	@PostMapping("/detalle_entregas")
	@ResponseStatus(HttpStatus.CREATED)
	public DetalleEntrega create(@RequestBody DetalleEntrega detalleEntrega) {
		return detalleEntregaService.save(detalleEntrega);
	}

	@PutMapping("/detalle_entregas/{id}")
	public DetalleEntrega update(@RequestBody DetalleEntrega detalleEntrega, @PathVariable Long id) {
		DetalleEntrega DetalleEntregaActual = detalleEntregaService.findById(id);

		DetalleEntregaActual.setCantidad_kg(detalleEntrega.getCantidad_kg());
		DetalleEntregaActual.setCalidad(detalleEntrega.getCalidad());
		DetalleEntregaActual.setValor_estimado(detalleEntrega.getValor_estimado());
		DetalleEntregaActual.setPuntos_calculados(detalleEntrega.getPuntos_calculados());



		return detalleEntregaService.save(DetalleEntregaActual);
	}

	@DeleteMapping("/detalle_entregas/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		detalleEntregaService.delete(id);
	}


}
