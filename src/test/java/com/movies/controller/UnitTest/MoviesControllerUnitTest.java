package com.movies.controller.UnitTest;

import com.movies.controller.MovieController;
import com.movies.model.Movie;
import com.movies.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class MovieControllerUnitTest {
    @InjectMocks
    private MovieController movieController;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    Model model;

    @Test
    void findAll(){
        when (movieRepository.findAll()).thenReturn(List.of(
                Movie.builder().id(1L).build()));
        String view = movieController.findAll(model);
        verify(movieRepository).findAll();
        assertEquals("movie-list", view);
    }

    @Test
    void findById(){
        Movie movie = Movie.builder().id(1L).build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        String view = movieController.findById(1L, model);
        assertEquals("movie-detail", view);
        verify(movieRepository).findById(1L);
        verify(model).addAttribute("movie", movie);
    }
    @Test
    void findById_MovieNotFound(){
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            movieController.findById(1L, model);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("movie not found", exception.getReason());
        verify(movieRepository).findById(1L);
        verify(model, never()).addAttribute(eq("movie"), any());
    }
    @Test
    void findById_IdNotFound(){
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            movieController.findById_NotExist(1L, model);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("movie not found", exception.getReason());
        verify(movieRepository).findById(1L);
        verify(model, never()).addAttribute(eq("movie"), any());
    }

    @Test
    void getFormCreateMovie(){
        Movie movie = new Movie();
        String view = movieController.getFormCreateMovie(model);
        assertEquals("movie-form", view);
        verify(model).addAttribute(eq("movie"), any(Movie.class));
    }

    @Test
    void getFormUpdateMovie(){
        Movie movie = Movie.builder().id(1L).build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        String view = movieController.getFormUpdateMovie(model, 1L);
        assertEquals("movie-form", view);
        verify(model).addAttribute("movie", movie);
        verify(movieRepository).findById(1L);
    }
    @Test
    void getFormUpdateMovie_NotFound(){
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            movieController.getFormUpdateMovie(model, 1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("not found", exception.getReason());
        verify(movieRepository).findById(1L);
        verify(model, never()).addAttribute(eq("movie"), any());
    }

    @Test
    void saveMovieNew(){
        Movie movie = new Movie();
        String view = movieController.saveMovie(movie);
        assertEquals("redirect:/movies", view);
        verify(movieRepository).save(movie);
    }

    @Test
    void saveMovieUpdate(){
        Movie movie = Movie.builder().id(1L).build();
        Movie movieUpdate = Movie.builder().id(1L).nombre("Editado").build();
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        String view = movieController.saveMovie(movieUpdate);
        assertEquals("redirect:/movies", view);
        verify(movieRepository).findById(1L);
        verify(movieRepository).existsById(1L);
        verify(movieRepository).save(movie);
        assertEquals(movieUpdate.getNombre(), movie.getNombre());
    }

    @Test
    void deleteMovie(){
        when(movieRepository.existsById(1L)).thenReturn(true);
        String view = movieController.deleteMovie(1L);
        assertEquals("redirect:/movies", view);
        verify(movieRepository).deleteById(1L);
    }
}
