package com.movies.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.movies.model.Movie;



@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {
}//