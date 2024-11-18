package com.movies.repository;

import com.movies.model.Categoria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends CrudRepository<Categoria, Integer> {
    Optional<Object> findById(Long id);

    boolean existsById(Long id);

    void deleteById(Long id);
}
