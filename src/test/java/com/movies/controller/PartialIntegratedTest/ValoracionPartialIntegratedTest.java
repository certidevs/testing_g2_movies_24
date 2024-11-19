package com.movies.controller.PartialIntegratedTest;

import com.movies.controller.ValoracionController;
import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ValoracionController.class)
public class ValoracionPartialIntegratedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValoracionRepository valoracionRepository;

    @Test
    void findAll() throws Exception {
        when(valoracionRepository.findAll()).thenReturn(List.of(
                Valoracion.builder()
                        .id(1L)
                        .customer(Customer.builder()
                                .id(1L)
                                .nombre("John")
                                .apellido("Doe")
                                .email("john.doe@example.com")
                                .password("password123")
                                .build())
                        .movie(Movie.builder()
                                .id(1L)
                                .name("Inception")
                                .duration(148)
                                .year(2010)
                                .build())
                        .puntuacion(5)
                        .comentario("Amazing movie!")
                        .build(),
                Valoracion.builder()
                        .id(2L)
                        .customer(Customer.builder()
                                .id(2L)
                                .nombre("Jane")
                                .apellido("Doe")
                                .email("jane.doe@example.com")
                                .password("password456")
                                .build())
                        .movie(Movie.builder()
                                .id(2L)
                                .name("Titanic")
                                .duration(195)
                                .year(1997)
                                .build())
                        .puntuacion(4)
                        .comentario("Great story.")
                        .build()
        ));

        mockMvc.perform(get("/valoraciones"))
                .andExpect(view().name("valoracion-list"))
                .andExpect(model().attributeExists("valoraciones"))
                .andExpect(model().attribute("valoraciones", hasSize(2)))
                .andExpect(model().attribute("valoraciones", hasItem(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("puntuacion", is(5)),
                                hasProperty("comentario", is("Amazing movie!"))
                        )
                )));
    }

    @Test
    void findById() throws Exception {
        var valoracion = Valoracion.builder()
                .id(1L)
                .customer(Customer.builder()
                        .id(1L)
                        .nombre("Juan")
                        .apellido("Perez")
                        .email("juan.perez@example.com")
                        .password("password789")
                        .build())
                .movie(Movie.builder()
                        .id(1L)
                        .name("Inception")
                        .duration(148)
                        .year(2010)
                        .build())
                .puntuacion(5)
                .comentario("Amazing movie!")
                .build();
        when(valoracionRepository.findById(1L)).thenReturn(Optional.of(valoracion));

        mockMvc.perform(get("/valoraciones/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("valoracion-detail"))
                .andExpect(model().attributeExists("valoracion"))
                .andExpect(model().attribute("valoracion", allOf(
                        hasProperty("id", is(1L)),
                        hasProperty("puntuacion", is(5)),
                        hasProperty("comentario", is("Amazing movie!"))
                )));

        verify(valoracionRepository).findById(1L);
    }

    @Test
    void save_createNew() throws Exception {
        mockMvc.perform(post("/valoraciones")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("customer.id", "1")
                        .param("movie.id", "1")
                        .param("puntuacion", "4")
                        .param("comentario", "Very entertaining."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/valoraciones"));

        verify(valoracionRepository).save(Mockito.any(Valoracion.class));
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(get("/valoraciones/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/valoraciones"));

        verify(valoracionRepository).deleteById(1L);
    }
}
