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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración con Selenium para la vista `valoracion-detail.html`.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ValoracionDetailTest {

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
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    @DisplayName("Comprobar valoración OK con todos los datos correctos")
    void valoracionExistWithAllDetails() {
        // Crear datos de prueba en la base de datos
        Customer customer = customerRepository.save(Customer.builder().nombre("John").build());
        Movie movie = movieRepository.save(Movie.builder().name("Inception").build());
        Valoracion valoracion = valoracionRepository.save(Valoracion.builder().customer(customer).movie(movie).puntuacion(5).comentario("Amazing movie!").build());

        // Navegar a la página detalle de la valoración
        driver.get("http://localhost:8080/valoraciones/" + valoracion.getId());

        WebElement h1 = driver.findElement(By.tagName("h1"));
        assertEquals("Detalle de Valoración " + valoracion.getId(), h1.getText());

        assertEquals("John", driver.findElement(By.id("valoracion-customer")).getText());
        assertEquals("Inception", driver.findElement(By.id("valoracion-movie")).getText());
        assertEquals("5", driver.findElement(By.id("valoracion-puntuacion")).getText());
        assertEquals("Amazing movie!", driver.findElement(By.id("valoracion-comentario")).getText());
    }

    @Test
    @DisplayName("Comprobar valores nulos o no disponibles")
    void checkNullValues() {
        Valoracion valoracion = valoracionRepository.save(Valoracion.builder().puntuacion(3).comentario("Average.").build());
        driver.get("http://localhost:8080/valoraciones/" + valoracion.getId());

        // Verificar campos vacíos para customer y movie
        WebElement customerEmpty = driver.findElement(By.id("valoracion-customer-empty"));
        assertEquals("Sin cliente", customerEmpty.getText());

        WebElement movieEmpty = driver.findElement(By.id("valoracion-movie-empty"));
        assertEquals("Sin película", movieEmpty.getText());
    }

    @Test
    @DisplayName("Comprobar acción editar, borrar y volver")
    void actionButtons() {
        Customer customer = customerRepository.save(Customer.builder().nombre("Jane").build());
        Movie movie = movieRepository.save(Movie.builder().name("Avatar").build());
        Valoracion valoracion = valoracionRepository.save(Valoracion.builder().customer(customer).movie(movie).puntuacion(4).comentario("Good movie!").build());
        driver.get("http://localhost:8080/valoraciones/" + valoracion.getId());

        // Botón editar
        var editBtn = driver.findElement(By.id("editButton"));
        assertEquals("Editar", editBtn.getText());
        assertEquals(
                "http://localhost:8080/valoraciones/edit/" + valoracion.getId(),
                editBtn.getAttribute("href")
        );
        editBtn.click();
        assertEquals(
                "http://localhost:8080/valoraciones/edit/" + valoracion.getId(),
                driver.getCurrentUrl()
        );
        driver.navigate().back();

        // Botón volver
        var backBtn = driver.findElement(By.id("backButton"));
        assertEquals("Volver a la lista", backBtn.getText());
        assertEquals("http://localhost:8080/valoraciones", backBtn.getAttribute("href"));
        backBtn.click();
        assertEquals("http://localhost:8080/valoraciones", driver.getCurrentUrl());
        driver.navigate().back();

        // Botón borrar
        var deleteBtn = driver.findElement(By.id("deleteButton"));
        assertEquals("Borrar", deleteBtn.getText());
        assertEquals(
                "http://localhost:8080/valoraciones/delete/" + valoracion.getId(),
                deleteBtn.getAttribute("href")
        );
        deleteBtn.click();
        assertEquals("http://localhost:8080/valoraciones", driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Comprobar valoración no existente")
    void valoracionNotExist() {
        driver.get("http://localhost:8080/valoraciones/999");

        assertEquals("Valoración no encontrada", driver.findElement(By.tagName("h1")).getText());
        assertEquals("No existe la valoración", driver.findElement(By.id("valoracion-empty")).getText());

        assertThrows(NoSuchElementException.class, () -> driver.findElement(By.id("editButton")));
        assertThrows(NoSuchElementException.class, () -> driver.findElement(By.id("deleteButton")));
    }
}
