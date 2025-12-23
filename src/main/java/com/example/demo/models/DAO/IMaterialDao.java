package com.example.demo.models.DAO;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.models.entity.Material;

public interface IMaterialDao extends CrudRepository<Material,Long>{

}
