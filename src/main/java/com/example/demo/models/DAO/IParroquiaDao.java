package com.example.demo.models.DAO;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.models.entity.Parroquia;

public interface IParroquiaDao extends CrudRepository<Parroquia, Long> {

}
