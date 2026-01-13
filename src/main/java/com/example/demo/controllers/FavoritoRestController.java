package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.entity.Favorito;
import com.example.demo.models.service.IFavoritoService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FavoritoRestController {

    @Autowired
    private IFavoritoService favoritoService;

    @GetMapping("/favoritos/usuario/{cedula}")
    public List<Favorito> listarPorUsuario(@PathVariable Long cedula) {
        return favoritoService.findByUsuario(cedula);
    }

    @PostMapping("/favoritos")
    @ResponseStatus(HttpStatus.CREATED)
    public Favorito create(@RequestBody Favorito favorito) {
        return favoritoService.save(favorito);
    }

    @DeleteMapping("/favoritos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @RequestParam Long cedula,
        @RequestParam Long idUbicacion
    ) {
        favoritoService.eliminarPorIds(cedula, idUbicacion);
    }
    
    @DeleteMapping("/favoritos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        favoritoService.deleteById(id); 
    }
    
}
