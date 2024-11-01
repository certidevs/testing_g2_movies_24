package com.movies.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;

    // Las lineas comentadas de abajo dan error, por que supuestamente ya estan definidas,
    // pero las dejo ahi por si acaso

    //public Categoria(Long id, String nombre, String descripcion ) {
    //    this.id = id;
    //    this.nombre = nombre;
    //    this.descripcion = descripcion;
    // }
}

