package com.movies.selenium.valoracion;

import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
Test de Selenium para probar: valoracion-form.html
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ValoracionFormTest {

    @Autowired
    private ValoracionRepository valoracionRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private MovieRepository movieRepository;

    WebDriver driver;

    @BeforeEach
    void setUp() {
        valoracionRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
        movieRepository.deleteAllInBatch();
        driver = new ChromeDriver(); // Inicializa el driver de Selenium (Chrome)
    }

    @AfterEach
    void tearDown() {
        driver.quit(); // Cierra el navegador después de cada test
    }

    @Test
    @DisplayName("Comprobar inputs vacíos si es CREACIÓN")
    void checkCreation_EmptyInputs() {
        customerRepository.saveAll(List.of(
                Customer.builder().nombre("cliente 1").build(),
                Customer.builder().nombre("cliente 2").build()
        ));
        movieRepository.saveAll(List.of(
                Movie.builder().name("pelicula 1").build(),
                Movie.builder().name("pelicula 2").build()
        ));

        driver.get("http://localhost:8080/valoraciones/new");

        var h1 = driver.findElement(By.tagName("h1"));
        assertEquals("Crear Valoración", h1.getText());

        // Comprobar inputs vacíos
        var inputComentario = driver.findElement(By.id("comentario"));
        assertTrue(inputComentario.getAttribute("value").isEmpty());

        var inputPuntuacion = driver.findElement(By.id("puntuacion"));
        assertTrue(inputPuntuacion.getAttribute("value").isEmpty());

        // Selector de customer
        Select customerSelect = new Select(driver.findElement(By.id("customer")));
        assertFalse(customerSelect.isMultiple());
        assertEquals(3, customerSelect.getOptions().size());
        assertEquals("", customerSelect.getOptions().get(0).getText());
        assertEquals("CLIENTE 1", customerSelect.getOptions().get(1).getText());
        assertEquals("CLIENTE 2", customerSelect.getOptions().get(2).getText());

        // Selector de movie
        Select movieSelect = new Select(driver.findElement(By.id("movie")));
        assertFalse(movieSelect.isMultiple());
        assertEquals(3, movieSelect.getOptions().size());
        assertEquals("", movieSelect.getOptions().get(0).getText());
        assertEquals("PELICULA 1", movieSelect.getOptions().get(1).getText());
        assertEquals("PELICULA 2", movieSelect.getOptions().get(2).getText());
    }

    @Test
    @DisplayName("Comprobar que el formulario aparece relleno al editar una valoración")
    void checkEdition_FilledInputs() {
        var customers = customerRepository.saveAll(List.of(
                Customer.builder().nombre("cliente 1").build(),
                Customer.builder().nombre("cliente 2").build()
        ));
        var movies = movieRepository.saveAll(List.of(
                Movie.builder().name("pelicula 1").build(),
                Movie.builder().name("pelicula 2").build()
        ));
        Customer customer2 = customers.get(1); // Cliente 2
        Movie movie2 = movies.get(1); // Película 2

        Valoracion valoracion = valoracionRepository.save(Valoracion.builder()
                .comentario("Gran película")
                .puntuacion(4)
                .customer(customer2)
                .movie(movie2)
                .build());

        driver.get("http://localhost:8080/valoraciones/edit/" + valoracion.getId());

        // Comprobar inputs rellenos
        var inputComentario = driver.findElement(By.id("comentario"));
        assertEquals("Gran película", inputComentario.getAttribute("value"));

        var inputPuntuacion = driver.findElement(By.id("puntuacion"));
        assertEquals("4", inputPuntuacion.getAttribute("value"));

        // Selector de customer
        Select customerSelect = new Select(driver.findElement(By.id("customer")));
        assertFalse(customerSelect.isMultiple());
        assertEquals(3, customerSelect.getOptions().size());
        assertEquals(
                String.valueOf(customer2.getId()),
                customerSelect.getFirstSelectedOption().getAttribute("value")
        );
        assertEquals(customer2.getNombre(), customerSelect.getFirstSelectedOption().getText());

        // Selector de movie
        Select movieSelect = new Select(driver.findElement(By.id("movie")));
        assertFalse(movieSelect.isMultiple());
        assertEquals(3, movieSelect.getOptions().size());
        assertEquals(
                String.valueOf(movie2.getId()),
                movieSelect.getFirstSelectedOption().getAttribute("value")
        );
        assertEquals(movie2.getName(), movieSelect.getFirstSelectedOption().getText());
    }

    @Test
    @DisplayName("Entrar en el formulario y crear una nueva valoración y enviar")
    void crearNuevaValoracionYEnviar() {
        // Implementación pendiente...
    }

    @Test
    @DisplayName("Entrar en el formulario y editar una valoración existente y enviar")
    void editarValoracionYEnviar() {
        // Implementación pendiente...
    }

    // Casos límite y validaciones: qué pasa si pongo valores erróneos en todos los campos
}
