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
    @JoinTable(
            name = "movie_users",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Customer> customer = new HashSet<>();

   // @ManyToOne
  //  @JoinColumn(name = "category_id", nullable = false)
  //  private Category category;

}
