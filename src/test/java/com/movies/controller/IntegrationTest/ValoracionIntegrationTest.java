package com.movies.controller.IntegrationTest;

import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ValoracionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        valoracionRepository.deleteAll();
        movieRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void findAll() throws Exception {
        // Limpiar la base de datos
        valoracionRepository.deleteAll();
        customerRepository.deleteAll();
        movieRepository.deleteAll();

        // Crear datos de prueba
        Customer customer1 = customerRepository.save(Customer.builder()
                .nombre("Ana")
                .apellido("C")
                .email("ana.c@example.com")
                .password("password")
                .build());

        Customer customer2 = customerRepository.save(Customer.builder()
                .nombre("P")
                .apellido("C")
                .email("p.c@example.com")
                .password("password")
                .build());

        Movie movie1 = movieRepository.save(Movie.builder()
                .name("Inception")
                .year(2010)
                .duration(148)
                .rentalPricePerDay(5.00)
                .build());

        Movie movie2 = movieRepository.save(Movie.builder()
                .name("Titanic")
                .year(1997)
                .duration(195)
                .rentalPricePerDay(5.00)
                .build());

        Valoracion valoracion1 = valoracionRepository.save(Valoracion.builder()
                .customer(customer1)
                .movie(movie1)
                .comentario("Amazing movie!")
                .puntuacion(5)
                .build());

        Valoracion valoracion2 = valoracionRepository.save(Valoracion.builder()
                .customer(customer2)
                .movie(movie2)
                .comentario("Emotional story!")
                .puntuacion(4)
                .build());

        // Ejecutar el test
        mockMvc.perform(get("/valoraciones"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("valoraciones"))
                .andExpect(model().attribute("valoraciones", hasSize(2)))
                .andExpect(model().attribute("valoraciones", hasItem(
                        allOf(
                                hasProperty("id", is(valoracion1.getId())),
                                hasProperty("comentario", is(valoracion1.getComentario())),
                                hasProperty("puntuacion", is(valoracion1.getPuntuacion()))
                        )
                )))
                .andExpect(model().attribute("valoraciones", hasItem(
                        allOf(
                                hasProperty("id", is(valoracion2.getId())),
                                hasProperty("comentario", is(valoracion2.getComentario())),
                                hasProperty("puntuacion", is(valoracion2.getPuntuacion()))
                        )
                )));
    }


    @Test
    void findById() throws Exception {
        // Crear datos de prueba
        Customer customer = customerRepository.save(Customer.builder()
                .nombre("Ana")
                .apellido("C")
                .email("ana.c@example.com")
                .password("password")
                .build());

        Movie movie = movieRepository.save(Movie.builder()
                .name("Inception")
                .year(2010)
                .duration(148)
                .rentalPricePerDay(5.00)
                .build());

        Valoracion valoracion = valoracionRepository.save(Valoracion.builder()
                .customer(customer)
                .movie(movie)
                .comentario("Amazing movie!")
                .puntuacion(5)
                .build());

        // Verificar que la valoración se creó correctamente
        assertNotNull(valoracion.getId(), "La valoración debería haberse guardado y tener un ID.");

        // Imprimir la valoración para depurar
        System.out.println("Valoración creada: " + valoracion);

        // Ejecutar el test
        mockMvc.perform(get("/valoraciones/{id}", valoracion.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("valoracion-detail"))
                .andExpect(model().attributeExists("valoracion"))
                .andExpect(model().attribute("valoracion",
                        allOf(
                                hasProperty("id", is(valoracion.getId())),
                                hasProperty("comentario", is(valoracion.getComentario())),
                                hasProperty("puntuacion", is(valoracion.getPuntuacion())),
                                hasProperty("customer", hasProperty("id", is(customer.getId()))),
                                hasProperty("movie", hasProperty("id", is(movie.getId())))
                        )));
    }

    @Test
    void findById_NotExist() throws Exception {
        mockMvc.perform(get("/valoraciones/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFormToCreateValoracion() throws Exception {
        mockMvc.perform(get("/valoraciones/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("valoracion-form"))
                .andExpect(model().attributeExists("valoracion"));
    }

    @Test
    void getFormToUpdateValoracion() throws Exception {
        // Crear datos de prueba
        Customer customer = customerRepository.save(Customer.builder()
                .nombre("Ana")
                .apellido("C")
                .email("ana.c@example.com")
                .password("password")
                .build());

        Movie movie = movieRepository.save(Movie.builder()
                .name("Inception")
                .year(2010)
                .duration(148)
                .rentalPricePerDay(5.00)
                .build());

        Valoracion valoracion = valoracionRepository.save(Valoracion.builder()
                .customer(customer)
                .movie(movie)
                .comentario("Amazing movie!")
                .puntuacion(5)
                .build());

        // Ejecutar el test
        mockMvc.perform(get("/valoraciones/edit/{id}", valoracion.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("valoracion-form"))
                .andExpect(model().attributeExists("valoracion"));
    }

    @Test
    void saveValoracion() throws Exception {
        // Crear datos de prueba
        Customer customer = customerRepository.save(Customer.builder()
                .nombre("Ana")
                .apellido("C")
                .email("ana.c@example.com")
                .password("password")
                .build());

        Movie movie = movieRepository.save(Movie.builder()
                .name("Inception")
                .year(2010)
                .duration(148)
                .rentalPricePerDay(5.00)
                .build());

        // Confirmar IDs generados
        System.out.println("Customer ID: " + customer.getId());
        System.out.println("Movie ID: " + movie.getId());

        // Enviar datos
        mockMvc.perform(MockMvcRequestBuilders.post("/valoraciones")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("customer.id", String.valueOf(customer.getId()))
                        .param("movie.id", String.valueOf(movie.getId()))
                        .param("comentario", "Amazing movie!")
                        .param("puntuacion", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/valoraciones"));

        // Verificar los datos guardados
        valoracionRepository.findAll().forEach(valoracion -> System.out.println("Saved Valoracion: " + valoracion));
        Valoracion savedValoracion = valoracionRepository.findAll().get(0);
        assertEquals("Amazing movie!", savedValoracion.getComentario());
        assertEquals(5, savedValoracion.getPuntuacion());
        assertEquals(customer.getId(), savedValoracion.getCustomer().getId());
        assertEquals(movie.getId(), savedValoracion.getMovie().getId());
    }

    @Test
    void deleteValoracion() throws Exception {
        // Crear datos de prueba
        Customer customer = customerRepository.save(Customer.builder()
                .nombre("Ana")
                .apellido("C")
                .email("ana.c@example.com")
                .password("password")
                .build());

        Movie movie = movieRepository.save(Movie.builder()
                .name("Inception")
                .year(2010)
                .duration(148)
                .rentalPricePerDay(5.00)
                .build());

        Valoracion valoracion = valoracionRepository.save(Valoracion.builder()
                .customer(customer)
                .movie(movie)
                .comentario("Amazing movie!")
                .puntuacion(5)
                .build());

        // Confirmar que la valoración fue creada
        System.out.println("Valoracion ID antes de eliminar: " + valoracion.getId());

        // Ejecutar la solicitud GET para eliminar la valoración
        mockMvc.perform(get("/valoraciones/delete/{id}", valoracion.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/valoraciones"));

        // Verificar que la valoración ya no existe
        boolean valoracionExiste = valoracionRepository.findById(valoracion.getId()).isPresent();
        assertFalse(valoracionExiste, "La valoración debería haber sido eliminada.");
    }

}
