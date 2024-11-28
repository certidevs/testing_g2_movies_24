package com.movies.controller.PartialIntegratedTest;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ValoracionPartialIntegratedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValoracionRepository valoracionRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private MovieRepository movieRepository;

    @Test
    void findAll() throws Exception {
        when(valoracionRepository.findAll()).thenReturn(List.of(
                Valoracion.builder().id(1L).build(),
                Valoracion.builder().id(2L).build()
        ));

        mockMvc.perform(get("/valoraciones"))
                .andExpect(status().isOk())
                .andExpect(view().name("valoracion-list"))
                .andExpect(model().attributeExists("valoraciones"))
                .andExpect(model().attribute("valoraciones", hasSize(2)));
    }

    @Test
    void findById() throws Exception {
        // Crear un objeto Customer simulado
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setNombre("Juan Perez");

        // Crear un objeto Movie simulado
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setName("Inception");

        // Crear una valoración simulada con el customer y la movie asociados
        Valoracion valoracion = Valoracion.builder()
                .id(1L)
                .customer(customer) // Asociar el customer
                .movie(movie) // Asociar la película
                .comentario("Excelente película")
                .puntuacion(5)
                .build();

        // Configurar el mock del repositorio para devolver la valoración simulada
        when(valoracionRepository.findById(1L)).thenReturn(Optional.of(valoracion));

        // Realizar la solicitud GET y validar el comportamiento
        mockMvc.perform(get("/valoraciones/{id}", 1L))
                .andExpect(status().isOk()) // Estado HTTP 200
                .andExpect(view().name("valoracion-detail")) // Vista esperada
                .andExpect(model().attributeExists("valoracion")) // Modelo contiene "valoracion"
                .andExpect(model().attribute("valoracion", valoracion)); // Validar el modelo
    }




    @Test
    void findById_ValoracionNotFound() throws Exception {
        when(valoracionRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/valoraciones/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_IdNotFound() throws Exception {
        // Configurar el mock del repositorio para devolver un Optional vacío
        when(valoracionRepository.findById(1L)).thenReturn(Optional.empty());

        // Realizar la solicitud GET y validar el comportamiento
        mockMvc.perform(get("/valoraciones/{id}", 1L))
                .andExpect(status().isNotFound()) // Estado HTTP 404
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("Valoración no encontrada",
                        ((ResponseStatusException) result.getResolvedException()).getReason()));

        // Verificar que el método del repositorio fue llamado
        verify(valoracionRepository).findById(1L);
    }





    @Test
    void getFormCreateValoracion() throws Exception {
        mockMvc.perform(post("/valoraciones")
                        .param("comentario", "Película increíble")
                        .param("puntuacion", "8")
                        .param("customer.id", "1")
                        .param("movie.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/valoraciones"));

        verify(valoracionRepository).save(any(Valoracion.class));
    }

    @Test
    void getFormUpdateValoracion() throws Exception {
        Valoracion valoracion = Valoracion.builder()
                .id(1L)
                .comentario("Comentario original")
                .puntuacion(7)
                .build();

        when(valoracionRepository.findById(1L)).thenReturn(Optional.of(valoracion));
        when(customerRepository.findAll()).thenReturn(List.of(
                Customer.builder().id(1L).nombre("Cliente 1").build()
        ));
        when(movieRepository.findAll()).thenReturn(List.of(
                Movie.builder().id(1L).name("Película 1").build()
        ));

        mockMvc.perform(get("/valoraciones/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("valoracion-form"))
                .andExpect(model().attributeExists("valoracion"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(model().attributeExists("movies"));
    }


    @Test
    void getFormUpdateValoracion_NotFound() throws Exception {
        // Configurar el mock para devolver un Optional vacío
        when(valoracionRepository.findById(1L)).thenReturn(Optional.empty());

        // Realizar la solicitud GET para actualizar una valoración inexistente
        mockMvc.perform(get("/valoraciones/update/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveValoracionNew() throws Exception {
        mockMvc.perform(post("/valoraciones")
                        .param("comentario", "Nueva valoración")
                        .param("puntuacion", "5")
                        .param("customer.id", "1")
                        .param("movie.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/valoraciones"));

        verify(valoracionRepository).save(any(Valoracion.class));
    }

    @Test
    void saveValoracionUpdate() throws Exception {
        // Crear una valoración de prueba
        Valoracion valoracion = new Valoracion();
        valoracion.setId(1L);
        valoracion.setComentario("Comentario original");
        valoracion.setPuntuacion(7);

        // Configurar el mock para simular la actualización
        when(valoracionRepository.save(any(Valoracion.class))).thenReturn(valoracion);

        // Ejecutar la solicitud POST para actualizar la valoración
        mockMvc.perform(post("/valoraciones")
                        .param("id", "1")
                        .param("comentario", "Comentario actualizado")
                        .param("puntuacion", "9")
                        .param("customer.id", "1")
                        .param("movie.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/valoraciones"));

        // Verificar que se llamó al método save del repositorio
        verify(valoracionRepository, times(1)).save(any(Valoracion.class));
    }

    @Test
    void deleteValoracion() throws Exception {
        when(valoracionRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/valoraciones/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/valoraciones"));

        verify(valoracionRepository).deleteById(1L);
    }
}
