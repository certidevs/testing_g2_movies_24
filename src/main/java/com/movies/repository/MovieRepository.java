package com.movies.repository;

import com.movies.model.Customer;
import com.movies.model.Movie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {
}