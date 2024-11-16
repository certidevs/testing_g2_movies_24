package com.movies.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String name;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    private Integer duration;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1888, message = "El año debe ser mayor a 1888")
    @Max(value = 2100, message = "El año debe ser menor a 2100")
    private Integer year;

    @ManyToMany
    @JoinColumn(name = "customer.id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "categoria.id", nullable = false)
    private Categoria category;

}//
