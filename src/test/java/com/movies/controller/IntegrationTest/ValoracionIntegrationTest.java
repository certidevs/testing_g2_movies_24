package com.movies.controller.IntegrationTest;

import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración completas para el controlador de valoraciones.
 * Utiliza el contexto completo de Spring y MockMvc para simular solicitudes HTTP.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Cada prueba se ejecuta dentro de una transacción que se revierte al final
class ValoracionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void findAll() throws Exception {
        // Crear datos de prueba en la base de datos
        Customer customer = customerRepository.save(Customer.builder().nombre("John").apellido("Doe").build());
        Movie movie = movieRepository.save(Movie.builder().name("Inception").year(2010).duration(148).build());
        valoracionRepository.save(Valoracion.builder().customer(customer).movie(movie).puntuacion(5).comentario("Great movie!").build());

        // Realizar solicitud GET para obtener todas las valoraciones
        mockMvc.perform(get("/valoraciones"))
                .andExpect(status().isOk())
                .andExpect(view().name("valoracion-list"))
                .andExpect(model().attributeExists("valoraciones"))
                .andExpect(model().attribute("valoraciones", hasSize(1)));
    }

    @Test
    void findById() throws Exception {
        // Crear datos de prueba en la base de datos
        Customer customer = customerRepository.save(Customer.builder().nombre("Jane").apellido("Doe").build());
        Movie movie = movieRepository.save(Movie.builder().name("Titanic").year(1997).duration(195).build());
        Valoracion valoracion = valoracionRepository.save(Valoracion.builder().customer(customer).movie(movie).puntuacion(4).comentario("Touching story!").build());

        // Realizar solicitud GET para obtener una valoración por ID
        mockMvc.perform(get("/valoraciones/{id}", valoracion.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("valoracion-detail"))
                .andExpect(model().attributeExists("valoracion"))
                .andExpect(model().attribute("valoracion", allOf(
                        hasProperty("id", is(valoracion.getId())),
                        hasProperty("puntuacion", is(4)),
                        hasProperty("comentario", is("Touching story!"))
                )));
    }

    @Test
    void save() throws Exception {
        // Crear datos de prueba para customer y movie
        Customer customer = customerRepository.save(Customer.builder().nombre("John").build());
        Movie movie = movieRepository.save(Movie.builder().name("Interstellar").build());

        // Realizar solicitud POST para crear una nueva valoración
        mockMvc.perform(post("/valoraciones")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("customer.id", String.valueOf(customer.getId()))
                        .param("movie.id", String.valueOf(movie.getId()))
                        .param("puntuacion", "5")
                        .param("comentario", "Masterpiece!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/valoraciones"));

        // Verificar que la valoración fue creada en la base de datos
        List<Valoracion> valoraciones = valoracionRepository.findAll();
        assertEquals(1, valoraciones.size());
        assertEquals("Masterpiece!", valoraciones.get(0).getComentario());
    }

    @Test
    void deleteById() throws Exception {
        // Crear datos de prueba en la base de datos
        Customer customer = customerRepository.save(Customer.builder().nombre("Jane").build());
        Movie movie = movieRepository.save(Movie.builder().name("Avatar").build());
        Valoracion valoracion = valoracionRepository.save(Valoracion.builder().customer(customer).movie(movie).puntuacion(4).build());

        // Realizar solicitud GET para eliminar la valoración
        mockMvc.perform(get("/valoraciones/delete/{id}", valoracion.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/valoraciones"));

        // Verificar que la valoración fue eliminada
        assertTrue(valoracionRepository.findById(valoracion.getId()).isEmpty());
    }

    @Test
    void obtenerFormularioParaNuevaValoracion() throws Exception {
        // Crear datos de prueba de Customer y Movie
        customerRepository.save(Customer.builder().nombre("John").build());
        movieRepository.save(Movie.builder().name("Interstellar").build());

        // Realizar solicitud GET para obtener el formulario de nueva valoración
        mockMvc.perform(get("/valoraciones/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("valoracion-form"))
                .andExpect(model().attributeExists("valoracion"));
    }

    @Test
    void obtenerFormularioParaEditarValoracion() throws Exception {
        // Crear datos de prueba en la base de datos
        Customer customer = customerRepository.save(Customer.builder().nombre("Jane").build());
        Movie movie = movieRepository.save(Movie.builder().name("Avatar").build());
        Valoracion valoracion = valoracionRepository.save(Valoracion.builder().customer(customer).movie(movie).puntuacion(4).comentario("Good movie!").build());

        // Realizar solicitud GET para obtener el formulario de edición
        mockMvc.perform(get("/valoraciones/edit/{id}", valoracion.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("valoracion-form"))
                .andExpect(model().attributeExists("valoracion"))
                .andExpect(model().attribute("valoracion", allOf(
                        hasProperty("id", is(valoracion.getId())),
                        hasProperty("puntuacion", is(4)),
                        hasProperty("comentario", is("Good movie!"))
                )));
    }

    @Test
    void validarCamposDeValoracion_Invalida() throws Exception {
        // Realizar solicitud POST con datos inválidos
        mockMvc.perform(post("/valoraciones")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("puntuacion", "11") // fuera de rango
                        .param("comentario", "")) // comentario vacío
                .andExpect(status().isBadRequest())
                .andExpect(view().name("valoracion-form"))
                .andExpect(model().attributeHasFieldErrors("valoracion", "puntuacion", "comentario"));
    }
}
