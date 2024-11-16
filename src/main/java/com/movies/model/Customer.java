package com.movies.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;

    @ManyToMany
    @JoinTable(
            name = "customer_movies",
            joinColumns = @JoinColumn(name = "customer.id"),
            inverseJoinColumns = @JoinColumn(name = "movie.id")
    )
    private Set<Movie> movies = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "valoracion.id")
    private Valoracion valoracion;
}
