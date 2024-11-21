package com.movies.repository;

import com.movies.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

        @Query("SELECT v FROM Valoracion v WHERE v.customer.id = :customerId")
        List<Valoracion> findByCustomerId(@Param("customerId") Long customerId);


}
