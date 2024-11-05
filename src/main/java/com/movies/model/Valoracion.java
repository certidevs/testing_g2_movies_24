package com.movies.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Data

public class Valoracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    private String comentario;
    private Integer puntuacion;

    public Valoracion(Customer customer, Movie movie, String comentario, Integer puntuacion) {
        this.customer = customer;
        this.movie = movie;
        this.comentario = comentario;
        this.puntuacion = puntuacion;
    }
}
