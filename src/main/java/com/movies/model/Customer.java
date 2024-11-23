package com.movies.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;


    @ManyToMany
    @ToString.Exclude
    @JoinTable(
            name = "customer_movies",
            joinColumns = @JoinColumn(name = "customer.id"),
           inverseJoinColumns = @JoinColumn(name = "movie.id")
    )
    private Set<Movie> movies = new HashSet<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Valoracion> valoraciones = new HashSet<>();
}
