package com.movies.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@Table(name = "movie")
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

//    @ManyToMany
//    @JoinTable(
//            name = "customer_movies",
//            joinColumns = @JoinColumn(name = "movie_id"),
//            inverseJoinColumns = @JoinColumn(name = "customer.id")
//    )
//    private Set<Customer> customer = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToMany(mappedBy = "movies")
    @ToString.Exclude
    private Set<Customer> customers = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Valoracion> valoraciones = new ArrayList<>();

}//
