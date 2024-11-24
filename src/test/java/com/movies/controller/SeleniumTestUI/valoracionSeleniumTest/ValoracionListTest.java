package com.movies.controller.SeleniumTestUI.valoracionSeleniumTest;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
Test funcional/UI de Selenium del listado de valoraciones valoracion-list.html.
Requiere la dependencia selenium-java.
Al poner DEFINED_PORT, el propio test inicia la aplicación de Spring Boot y ejecuta los tests con el navegador.
NO HACE FALTA INICIAR LA APLICACIÓN MANUALMENTE DESDE EL MAIN.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ValoracionListTest {

    @Autowired
    private ValoracionRepository valoracionRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private MovieRepository movieRepository;

    WebDriver driver;

    @BeforeEach
    void setUp() {
        valoracionRepository.deleteAll();
        customerRepository.deleteAll();
        movieRepository.deleteAll();

        driver = new ChromeDriver();
        driver.get("http://localhost:8080/valoraciones");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    @DisplayName("Comprobar etiqueta <title>")
    void title() {
        String title = driver.getTitle();
        assertEquals("Valoraciones List", title);
    }

    @Test
    @DisplayName("Comprobar la etiqueta <h1>")
    void h1() {
        WebElement h1 = driver.findElement(By.tagName("h1"));
        assertEquals("Lista de valoraciones", h1.getText());
    }

    @Test
    @DisplayName("Comprobar que existe el enlace de Crear nueva valoración y su texto")
    void buttonCreateValoracion() {
        WebElement createButton = driver.findElement(By.id("btnCreateValoracion"));
        assertEquals("Crear nueva valoración", createButton.getText());

        createButton.click(); // Pulsar botón para crear nueva valoración

        assertEquals("http://localhost:8080/valoraciones/new", driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Comprobar tabla vacía con texto cuando no hay datos")
    void tableEmpty() {
        // Comprobar que existe el mensaje de "No hay valoraciones"
        WebElement noValoracionesMessage = driver.findElement(By.id("valoracionesEmpty"));
        assertEquals("No hay valoraciones.", noValoracionesMessage.getText());

        // Comprobar que no existe la tabla de valoraciones
        assertThrows(
                NoSuchElementException.class,
                () -> driver.findElement(By.id("valoracionList"))
        );
    }

    @Test
    @DisplayName("Comprobar tabla con valoraciones")
    void tableWithValoraciones() {
        var customer = customerRepository.save(Customer.builder().nombre("Cliente 1").build());
        var movie = movieRepository.save(Movie.builder().name("Película 1").build());

        valoracionRepository.saveAll(List.of(
                Valoracion.builder().comentario("Comentario 1").puntuacion(5).customer(customer).movie(movie).build(),
                Valoracion.builder().comentario("Comentario 2").puntuacion(4).customer(customer).movie(movie).build(),
                Valoracion.builder().comentario("Comentario 3").puntuacion(3).customer(customer).movie(movie).build()
        ));

        driver.navigate().refresh(); // Refrescar la página

        WebElement valoracionList = driver.findElement(By.id("valoracionList"));
        assertTrue(valoracionList.isDisplayed());
    }

    @Test
    @DisplayName("Comprobar las columnas de la tabla")
    void tableWithValoraciones_columns() {
        var customer = customerRepository.save(Customer.builder().nombre("Cliente 1").build());
        var movie = movieRepository.save(Movie.builder().name("Película 1").build());

        valoracionRepository.saveAll(List.of(
                Valoracion.builder().comentario("Comentario 1").puntuacion(5).customer(customer).movie(movie).build(),
                Valoracion.builder().comentario("Comentario 2").puntuacion(4).customer(customer).movie(movie).build()
        ));

        driver.navigate().refresh(); // Refrescar la página

        WebElement valoracionList = driver.findElement(By.id("valoracionList"));

        // Obtener los encabezados de la tabla valoracionList
        List<WebElement> headers = valoracionList.findElements(By.tagName("th"));
        assertEquals(5, headers.size());
        assertEquals("ID", headers.get(0).getText());
        assertEquals("COMENTARIO", headers.get(1).getText());
        assertEquals("PUNTUACIÓN", headers.get(2).getText());
        assertEquals("CLIENTE", headers.get(3).getText());
        assertEquals("PELÍCULA", headers.get(4).getText());
    }

    @Test
    @DisplayName("Comprobar filas de la tabla y sus datos")
    void tableWithValoraciones_rows() {
        var customer = customerRepository.save(Customer.builder().nombre("Cliente 1").build());
        var movie = movieRepository.save(Movie.builder().name("Película 1").build());

        var valoracion = valoracionRepository.save(
                Valoracion.builder().comentario("Comentario 1").puntuacion(5).customer(customer).movie(movie).build()
        );

        driver.navigate().refresh(); // Refrescar la página

        WebElement id = driver.findElement(By.id("valoracionId_" + valoracion.getId()));
        assertEquals(valoracion.getId().toString(), id.getText());

        WebElement comentario = driver.findElement(By.id("valoracionComentario_" + valoracion.getId()));
        assertEquals("Comentario 1", comentario.getText());

        WebElement puntuacion = driver.findElement(By.id("valoracionPuntuacion_" + valoracion.getId()));
        assertEquals("5", puntuacion.getText());
    }
}
