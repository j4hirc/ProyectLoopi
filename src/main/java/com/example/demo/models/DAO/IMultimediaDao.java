package com.example.demo.models.DAO;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.models.entity.Multimedia;

public interface IMultimediaDao extends CrudRepository<Multimedia, Long> {

}
