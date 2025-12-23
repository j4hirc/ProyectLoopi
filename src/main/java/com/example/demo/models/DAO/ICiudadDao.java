package com.example.demo.models.DAO;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.models.entity.Ciudad;

public interface ICiudadDao extends CrudRepository<Ciudad, Long>{

}
