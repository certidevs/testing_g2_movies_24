package com.movies.controller.PartialIntegratedTest;

import com.movies.model.Categoria;
import com.movies.model.Categoria;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.CategoriaRepository;
import com.movies.repository.CategoriaRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoriaPartialIntegratedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaRepository categoriaRepository;

    @MockBean
    private MovieRepository movieRepository;

    @MockBean
    private CategoriaRepository customerRepository;

    @MockBean
    private ValoracionRepository valoracionRepository;

    @Test
    void findAll() throws Exception {
        when (categoriaRepository.findAll()).thenReturn(List.of(
                Categoria.builder().id(1L).build(),
                Categoria.builder().id(2L).build()));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("categoria-list"))
                .andExpect(model().attributeExists("categorias"))
                .andExpect(model().attribute("categorias", hasSize(2)));
    }

    @Test
    void findById() throws Exception {
        Categoria categoria = Categoria.builder().id(1L).build();

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        mockMvc.perform(get("/categorias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("categoria-detail"))
                .andExpect(model().attributeExists("categoria"));
    }

    @Test
    void findById_CategoriaNotFound() throws Exception{
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/categorias/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Categoria not found", ((ResponseStatusException) result.getResolvedException()).getReason()));

        verify(categoriaRepository).findById(1L);
    }

    @Test
    void findById_IdNotFound() throws Exception {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/categorias404/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Categoria not found", ((ResponseStatusException) result.getResolvedException()).getReason()));

        verify(categoriaRepository).findById(1L);
    }

    @Test
    void getFormCreateCategoria() throws Exception {
        mockMvc.perform(post("/categorias")
                        .param("nombre", "Cliente")
                        .param("apellido", "1")
                        .param("email", "123@gmail.com")
                        .param("phone", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias"));

        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void getFormUpdateCategoria() throws Exception {
        Categoria categoria = Categoria.builder().id(1L).build();

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        mockMvc.perform(get("/categorias/update/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("categoria-form"))
                .andExpect(model().attributeExists("categoria"))
                .andExpect(model().attribute("categoria", categoria));
    }

    @Test
    void getFormUpdateCategoria_NotFound() throws Exception{
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/categorias/update/{id}", 1L))
                .andExpect(status().isNotFound());
    }
    @Test
    void saveCategoriaNew () throws Exception {
        mockMvc.perform(post("/categorias")
                        .param("nombre", "Cliente")
                        .param("apellido", "1")
                        .param("email", "123@gmail.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias"));

        verify(categoriaRepository).save(any(Categoria.class));
    }
    @Test
    void saveCategoriaUpdate() throws Exception {
        Categoria categoria = Categoria.builder().id(1L).nombre("Cliente").build();
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        mockMvc.perform(post("/categorias")
                        .param("id", "1")
                        .param("nombre", "Cliente Actualizado")
                        .param("apellido", "1")
                        .param("email", "123@gmail.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias"));

        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).save(any(Categoria.class));
    }
    @Test
    void deleteCategoria() throws Exception{
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        mockMvc.perform(get("/categorias/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias"));

        verify(categoriaRepository).deleteById(1L);
    }
    @Test
    void addMovieToCategoria() throws Exception {
        Categoria categoria = Categoria.builder().id(1L).build();
        //Categoria categoria = Categoria.builder().id(1L).build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        mockMvc.perform(post("/categorias/1/add-movie")
                        .param("id", "1")
                        .param("name", "Pelicula")
                        .param("duration", "60")
                        .param("year", "2021")
                        .param("categoriaId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias/1"));

        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).findById(1L);
        verify(movieRepository).save(any(Movie.class));
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void removeMovieFromCategoria() throws Exception {
        Categoria categoria = Categoria.builder().id(1L).build();
        Movie movie = Movie.builder().id(1L).build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        mockMvc.perform(post("/categorias/1/remove-movie/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias/1"));

        verify(categoriaRepository).findById(1L);
        verify(movieRepository).findById(1L);
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void addValoracionToCategoria() throws Exception {
        Categoria categoria = Categoria.builder().id(1L).build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        mockMvc.perform(post("/categorias/1/add-valoracion")
                        .param("puntuacion", "5")
                        .param("comentario", "Buen servicio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias/1"));

        verify(categoriaRepository).findById(1L);
        verify(valoracionRepository).save(any(Valoracion.class));
    }
    @Test
    void removeValoracionFromCategoria() throws Exception {
        Categoria categoria = Categoria.builder().id(1L).build();
        Valoracion valoracion = Valoracion.builder().id(1L).build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(valoracionRepository.findById(1L)).thenReturn(Optional.of(valoracion));
        mockMvc.perform(post("/categorias/1/remove-valoracion/{valoracionId}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categorias/1"));

        verify(categoriaRepository).findById(1L);
        verify(valoracionRepository).findById(1L);
        verify(categoriaRepository).save(any(Categoria.class));
    }
}
