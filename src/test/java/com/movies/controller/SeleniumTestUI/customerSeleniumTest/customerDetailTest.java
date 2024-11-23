package com.movies.controller.SeleniumTestUI.customerSeleniumTest;

import com.movies.model.Categoria;
import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.CategoriaRepository;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.openqa.selenium.WebDriver;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest

public class customerDetailTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    WebDriver driver;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAllInBatch();
        movieRepository.deleteAllInBatch();
        categoriaRepository.deleteAllInBatch();
        valoracionRepository.deleteAllInBatch();
        driver = new ChromeDriver();
    }
    @AfterEach
    void tearDown() {
        driver.quit();
    }
    @Test
    public void testCustomerDetailPage() {

        Customer customer = customerRepository.save(Customer.builder().id(1L).nombre("Ana").apellido("C").email("ana.c@example.com").build());
        driver.get("http://localhost:8080/customers/" + customer.getId());

        String pageTitle = driver.findElement(By.id("title_customer_detail")).getText();
        assertEquals("Datos del Cliente", pageTitle);

        String header = driver.findElement(By.id("h1_customer_detail")).getText();
        assertEquals("Detalle del Cliente", header);

        String customerId = driver.findElement(By.id("customer_id")).getText();
        assertTrue(customerId.matches("\\d+"), "El ID del cliente debe ser numérico");

        String nombre = driver.findElement(By.id("nombre")).getText();
        assertEquals("Ana", nombre);

        String apellido = driver.findElement(By.id("apellido")).getText();
        assertEquals("C", apellido);

        String email = driver.findElement(By.id("email")).getText();
        assertEquals("ana.c@example.com", email);

        WebElement editButton = driver.findElement(By.id("editBtn_customer"));
        assertTrue(editButton.isDisplayed());
        assertEquals("Editar", editButton.getText());

        WebElement deleteButton = driver.findElement(By.id("deleteBtn_customer"));
        assertTrue(deleteButton.isDisplayed());
        assertEquals("Borrar", deleteButton.getText());

        WebElement backButton = driver.findElement(By.id("backBtn_customer_list"));
        assertTrue(backButton.isDisplayed());
        assertEquals("Volver a la lista", backButton.getText());
    }

    @Test
    public void testCustomerMovies() {
        Set<Movie> movies = new HashSet<>();
        movies.add(Movie.builder().id(1L).name("Inception").build());
        Customer customer = customerRepository.save(Customer.builder().id(1L).nombre("Ana").apellido("C").email("ana.c@example.com").password("123").movies(movies).build());
        driver.get("http://localhost:8080/customers/" + customer.getId());

        WebElement moviesHeader = driver.findElement(By.id("customer_movie"));
        assertEquals("Ver Películas del usuario", moviesHeader.getText());

        WebElement firstMovie = driver.findElement(By.id("customer_movie_name"));
        assertTrue(firstMovie.isDisplayed());
        assertEquals("Inception", firstMovie.getText());
    }

    @Test
    public void testCustomerValoraciones() {
        Set < Valoracion > valoraciones = new HashSet<>();
        valoraciones.add(Valoracion.builder().id(1L).puntuacion(5).comentario("Muy buena").build());
        Set <Categoria> categorias = new HashSet<>();
        categorias.add(Categoria.builder().id(1L).nombre("Acción").build());
        Set<Movie> movies = new HashSet<>();
        Movie.MovieBuilder movieBuilder = Movie.builder().id(1L).name("Inception").duration(60).year(2025).valoraciones(valoraciones);
        for (Categoria categoria : categorias) {
            movieBuilder.categoria(categoria);
        }
        Customer customer = customerRepository.save(Customer.builder().id(1L).nombre("Ana").apellido("C").email("ana.c@example.com").password("123").movies(movies).valoraciones(valoraciones).build());
        driver.get("http://localhost:8080/customers/" + customer.getId());

        WebElement valoracionesHeader = driver.findElement(By.id("customer_valoracion"));
        assertEquals("Ver valoraciones usuario", valoracionesHeader.getText());

        WebElement firstValoracion = driver.findElement(By.id("customer_valoracion_movie"));
        assertTrue(firstValoracion.isDisplayed());
        assertEquals("Inception", firstValoracion.getText());
    }
}



