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
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;

    @ManyToMany
    @JoinColumn(name = "movies.id")
    private Movie movie;

    @OneToMany
    @JoinColumn(name = "valoracion.id")
    private Valoracion valoracion;
}
