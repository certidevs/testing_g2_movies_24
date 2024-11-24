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

/**
 * Pruebas de integración parcial para el controlador ValoracionController.
 * Se simulan las dependencias del repositorio usando @MockBean.
 * Utiliza MockMvc para realizar solicitudes HTTP simuladas.
 */
@WebMvcTest(ValoracionController.class)
public class ValoracionPartialIntegratedTest {

    // Herramienta para simular peticiones HTTP
    @Autowired
    private MockMvc mockMvc;

    // Simulación del repositorio
    @MockBean
    private ValoracionRepository valoracionRepository;

    @Test
    void findAll() throws Exception {
        // Configurar datos simulados
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
                        .build()
        ));

        // Realizar una solicitud GET a /valoraciones y verificar el resultado
        mockMvc.perform(get("/valoraciones"))
                .andExpect(status().isOk()) // Verifica que el estado HTTP sea 200
                .andExpect(view().name("valoracion-list")) // Verifica que se carga la vista correcta
                .andExpect(model().attributeExists("valoraciones")) // Verifica que el modelo contiene el atributo "valoraciones"
                .andExpect(model().attribute("valoraciones", hasSize(1))) // Verifica que hay una valoración en la lista
                .andExpect(model().attribute("valoraciones", hasItem(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("puntuacion", is(5)),
                                hasProperty("comentario", is("Amazing movie!"))
                        )
                )));
    }

    @Test
    void findById_WhenValoracionExists() throws Exception {
        // Configurar datos simulados
        Valoracion valoracion = Valoracion.builder()
                .id(1L)
                .customer(Customer.builder().id(1L).nombre("John").build())
                .movie(Movie.builder().id(1L).name("Inception").build())
                .puntuacion(5)
                .comentario("Outstanding!")
                .build();
        when(valoracionRepository.findById(1L)).thenReturn(Optional.of(valoracion));

        // Realizar una solicitud GET a /valoraciones/{id} y verificar el resultado
        mockMvc.perform(get("/valoraciones/{id}", 1L))
                .andExpect(status().isOk()) // Verifica que el estado HTTP sea 200
                .andExpect(view().name("valoracion-detail")) // Verifica que se carga la vista correcta
                .andExpect(model().attributeExists("valoracion")) // Verifica que el modelo contiene "valoracion"
                .andExpect(model().attribute("valoracion", allOf(
                        hasProperty("id", is(1L)),
                        hasProperty("puntuacion", is(5)),
                        hasProperty("comentario", is("Outstanding!"))
                )));
    }

    @Test
    void findById_WhenValoracionNotExists() throws Exception {
        // Configurar que el repositorio no devuelva ninguna valoración
        when(valoracionRepository.findById(1L)).thenReturn(Optional.empty());

        // Realizar una solicitud GET a /valoraciones/{id} y verificar que devuelve 404
        mockMvc.perform(get("/valoraciones/{id}", 1L))
                .andExpect(status().isNotFound()); // Verifica que el estado HTTP sea 404
    }

    @Test
    void save_CreateNewValoracion() throws Exception {
        // Simular la creación de una nueva valoración mediante una solicitud POST
        mockMvc.perform(post("/valoraciones")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("customer.id", "1") // Enviar parámetros del formulario
                        .param("movie.id", "1")
                        .param("puntuacion", "4")
                        .param("comentario", "Very entertaining."))
                .andExpect(status().is3xxRedirection()) // Verifica que redirige después de guardar
                .andExpect(redirectedUrl("/valoraciones")); // Verifica que redirige a /valoraciones

        // Verifica que se llama al método save del repositorio
        verify(valoracionRepository).save(Mockito.any(Valoracion.class));
    }

    @Test
    void deleteById() throws Exception {
        // Simular la eliminación de una valoración mediante una solicitud GET
        mockMvc.perform(get("/valoraciones/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection()) // Verifica que redirige después de eliminar
                .andExpect(redirectedUrl("/valoraciones")); // Verifica que redirige a /valoraciones

        // Verifica que se llama al método deleteById del repositorio
        verify(valoracionRepository).deleteById(1L);
    }
}
