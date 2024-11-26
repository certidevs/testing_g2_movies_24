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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ValoracionFormTest {

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
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    @DisplayName("Comprobar inputs vacíos si es CREACIÓN")
    void checkCreation_EmptyInputs() {
        customerRepository.save(Customer.builder().nombre("Ana").apellido("Perez").email("ana.p@example.com").build());
        movieRepository.save(Movie.builder().name("Inception").year(2010).build());

        driver.get("http://localhost:8080/valoraciones/new");

        var h1 = driver.findElement(By.tagName("h1"));
        assertEquals("Formulario de Valoración", h1.getText());

        var inputComentario = driver.findElement(By.id("comentario"));
        assertTrue(inputComentario.getAttribute("value").isEmpty());

        var inputPuntuacion = driver.findElement(By.id("puntuacion"));
        assertTrue(inputPuntuacion.getAttribute("value").isEmpty());

        Select customerSelect = new Select(driver.findElement(By.id("usuarioId")));
        assertFalse(customerSelect.isMultiple());
        assertEquals(2, customerSelect.getOptions().size());

        Select movieSelect = new Select(driver.findElement(By.id("peliculaId")));
        assertFalse(movieSelect.isMultiple());
        assertEquals(2, movieSelect.getOptions().size());
    }

    @Test
    @DisplayName("Comprobar que el formulario aparece relleno al editar una valoración")
    void checkEdition_FilledInputs() {
        var customer = customerRepository.save(Customer.builder().nombre("Ana").apellido("Perez").email("ana.p@example.com").build());
        var movie = movieRepository.save(Movie.builder().name("Inception").year(2010).build());

        var valoracion = valoracionRepository.save(Valoracion.builder()
                .customer(customer)
                .movie(movie)
                .comentario("Gran película")
                .puntuacion(5)
                .build());

        driver.get("http://localhost:8080/valoraciones/edit/" + valoracion.getId());

        var inputComentario = driver.findElement(By.id("comentario"));
        assertEquals("Gran película", inputComentario.getAttribute("value"));

        var inputPuntuacion = driver.findElement(By.id("puntuacion"));
        assertEquals("5", inputPuntuacion.getAttribute("value"));

        Select customerSelect = new Select(driver.findElement(By.id("usuarioId")));
        assertEquals(String.valueOf(customer.getId()), customerSelect.getFirstSelectedOption().getAttribute("value"));

        Select movieSelect = new Select(driver.findElement(By.id("peliculaId")));
        assertEquals(String.valueOf(movie.getId()), movieSelect.getFirstSelectedOption().getAttribute("value"));
    }

    @Test
    @DisplayName("Entrar en el formulario y crear una nueva valoración y enviar")
    void crearNuevaValoracionYEnviar() {
        var customer = customerRepository.save(Customer.builder().nombre("Ana").apellido("Perez").email("ana.p@example.com").build());
        var movie = movieRepository.save(Movie.builder().name("Inception").year(2010).build());

        driver.get("http://localhost:8080/valoraciones/new");

        var inputComentario = driver.findElement(By.id("comentario"));
        inputComentario.sendKeys("Excelente película");

        var inputPuntuacion = driver.findElement(By.id("puntuacion"));
        inputPuntuacion.sendKeys("9");

        Select customerSelect = new Select(driver.findElement(By.id("usuarioId")));
        customerSelect.selectByVisibleText("Ana Perez");

        Select movieSelect = new Select(driver.findElement(By.id("peliculaId")));
        movieSelect.selectByVisibleText("Inception");

        driver.findElement(By.id("btnSave")).click();

        assertEquals("http://localhost:8080/valoraciones", driver.getCurrentUrl());

        var valoracionGuardada = valoracionRepository.findAll().get(0);
        assertEquals("Excelente película", valoracionGuardada.getComentario());
        assertEquals(9, valoracionGuardada.getPuntuacion());
        assertEquals("Ana Perez", valoracionGuardada.getCustomer().getNombre() + " " + valoracionGuardada.getCustomer().getApellido());
    }
}
