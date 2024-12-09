package com.movies.RepositoryTest;

import com.movies.model.Categoria;
import com.movies.model.Movie;
import com.movies.repository.CategoriaRepository;
import com.movies.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoria;

    @BeforeEach
    public void setUp() {
       categoria = new Categoria();
        categoria.setNombre("Action");
        categoria = categoriaRepository.save(categoria);

        Movie movie1 = Movie.builder()
                .name("Movie 1")
                .duration(120)
                .year(2020)
                .categoria(categoria)
                .available(true)
                .rentalPricePerDay(5.99)
                .build();

        Movie movie2 = Movie.builder()
                .name("Movie 2")
                .duration(90)
                .year(1999)
                .categoria(categoria)
                .available(false)
                .rentalPricePerDay(3.99)
                .build();

        movieRepository.save(movie1);
        movieRepository.save(movie2);
    }

    @Test
    @DisplayName("Test find all peliculas disponibles")
    public void testFindAllAvailableMovies() {
        List<Movie> result = movieRepository.findAllAvailableMovies();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Movie 1");
    }

    @Test
    @DisplayName("Test find by categoria id")
    public void testFindByCategoriaId() {
        List<Movie> result = movieRepository.findByCategoriaId(categoria.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Test find por pelicula por rango de años")
    public void testFindMoviesByYearRange() {
        List<Movie> result = movieRepository.findMoviesByYearRange(1990, 2000);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Movie 2");
    }

    @Test
    @DisplayName("Test search peliculas por nombre")
    public void testSearchMoviesByName() {
        List<Movie> result = movieRepository.searchMoviesByName("Movie");
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Test find peliculas por precio de alquiler")
    public void testFindMoviesByRentalPriceLessThan() {
        List<Movie> result = movieRepository.findMoviesByRentalPriceLessThan(4.00);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Movie 2");
    }
}