package com.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movies.model.Movie;

import java.util.List;


@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

        @Query("SELECT m FROM Movie m JOIN m.customers c WHERE c.id = :customerId")
        List<Movie> findByCustomerId(@Param("customerId") Long customerId);

}//