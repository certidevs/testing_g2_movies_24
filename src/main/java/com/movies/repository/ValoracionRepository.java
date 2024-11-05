package com.movies.repository;

import com.movies.model.Valoracion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValoracionRepository extends CrudRepository<Valoracion, Integer> {
}
