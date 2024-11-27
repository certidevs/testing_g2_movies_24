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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ValoracionListTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    WebDriver driver;

    @BeforeEach
    void setUp() {
        valoracionRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
        movieRepository.deleteAllInBatch();
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/valoraciones");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    @DisplayName("Comprobar título de la página")
    void title() {
        String title = driver.getTitle();
        assertEquals("Lista de Valoraciones", title);
    }

    @Test
    @DisplayName("Comprobar encabezado <h1>")
    void h1() {
        WebElement h1 = driver.findElement(By.tagName("h1"));
        assertEquals("Lista de Valoraciones", h1.getText());
    }

    @Test
    @DisplayName("Comprobar que existe el botón de Crear nueva valoración")
    void buttonCreateValoracion() {
        WebElement createButton = driver.findElement(By.className("btn-primary"));
        assertEquals("Crear nueva Valoración", createButton.getText());

        createButton.click();
        assertEquals("http://localhost:8080/valoraciones/new", driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Comprobar tabla vacía con texto cuando no hay datos")
    void tableEmpty() {
        WebElement noValoracionesMessage = driver.findElement(By.tagName("p"));
        assertEquals("No hay valoraciones disponibles.", noValoracionesMessage.getText());

        assertThrows(
                NoSuchElementException.class,
                () -> driver.findElement(By.tagName("tbody"))
        );
    }

    @Test
    @DisplayName("Comprobar tabla con valoraciones")
    void tableWithValoraciones() {
        Customer customer = customerRepository.save(
                Customer.builder().nombre("Ana").apellido("Perez").email("ana.p@example.com").build()
        );
        Movie movie = movieRepository.save(
                Movie.builder().name("Inception").duration(148).year(2010).build()
        );
        valoracionRepository.saveAll(List.of(
                Valoracion.builder().customer(customer).movie(movie).comentario("Excelente película").puntuacion(9).build(),
                Valoracion.builder().customer(customer).movie(movie).comentario("Buena película").puntuacion(7).build()
        ));

        driver.navigate().refresh();

        WebElement valoracionTable = driver.findElement(By.tagName("table"));
        assertTrue(valoracionTable.isDisplayed());

        List<WebElement> rows = valoracionTable.findElements(By.tagName("tr"));
        assertEquals(3, rows.size()); // 1 encabezado + 2 filas de datos
    }

    @Test
    @DisplayName("Comprobar filas de la tabla con datos correctos")
    void tableWithValoraciones_rows() {
        Customer customer = customerRepository.save(
                Customer.builder().nombre("Ana").apellido("Perez").email("ana.p@example.com").build()
        );
        Movie movie = movieRepository.save(
                Movie.builder().name("Inception").duration(148).year(2010).build()
        );
        Valoracion valoracion = valoracionRepository.save(
                Valoracion.builder().customer(customer).movie(movie).comentario("Excelente película").puntuacion(9).build()
        );

        driver.navigate().refresh();

        WebElement table = driver.findElement(By.tagName("table"));
        WebElement firstRow = table.findElements(By.tagName("tr")).get(1);

        assertEquals(valoracion.getId().toString(), firstRow.findElements(By.tagName("td")).get(0).getText());
        assertEquals(customer.getNombre(), firstRow.findElements(By.tagName("td")).get(1).getText());
        assertEquals(movie.getName(), firstRow.findElements(By.tagName("td")).get(2).getText());
        assertEquals("9", firstRow.findElements(By.tagName("td")).get(3).getText());
        assertEquals("Excelente película", firstRow.findElements(By.tagName("td")).get(4).getText());
    }
}
