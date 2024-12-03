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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
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
        valoracionRepository.deleteAllInBatch();
       customerRepository.deleteAllInBatch();
       movieRepository.deleteAllInBatch();
       categoriaRepository.deleteAllInBatch();
        driver = new ChromeDriver();
    }
    @AfterEach
    void tearDown() {
        driver.quit();
    }
    @Test
    public void testCustomerDetailPage() {

        Customer customer = customerRepository.save(Customer.builder().id(1L).nombre("Ana").apellido("C").email("ana.c@example.com").password("123").build());
        driver.get("http://localhost:8080/customers/" + customer.getId());
        driver.navigate().refresh();

        String header = driver.findElement(By.id("h1_customer_detail")).getText();
        assertEquals("Datos del Cliente", header);

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
        Movie movie = movieRepository.save(Movie.builder().name("Inception").duration(148).year(2010).rentalPricePerDay(5.00).build());
        Set<Movie> movies = new HashSet<>();
        movies.add(movie);
        Customer customer = customerRepository.save(Customer.builder().id(1L).nombre("Ana").apellido("C").email("ana.c@example.com").password("123").movies(movies).build());
        driver.get("http://localhost:8080/customers/" + customer.getId());
        driver.navigate().refresh();
        WebElement moviesHeader = driver.findElement(By.id("customer_movie"));
        assertEquals("Ver Películas del usuario", moviesHeader.getText());

        WebElement firstMovie = driver.findElement(By.id("customer_movie_name"));
        assertTrue(firstMovie.isDisplayed());
        assertEquals("Inception", firstMovie.getText());
    }

    @Test
    public void testCustomerValoraciones() {
        Categoria categoria = Categoria.builder().id(1L).nombre("Acción").descripcion("Películas de acción").build();
        categoria = categoriaRepository.save(categoria);
        Movie movie = Movie.builder().id(1L).name("Inception").duration(148).year(2010).rentalPricePerDay(5.00).categoria(categoria).build();
        movie = movieRepository.save(movie);
        Customer customer = customerRepository.save(Customer.builder().id(1L).nombre("Ana").apellido("C").email("ana.c@example.com").password("123").build());
        Valoracion valoracion = Valoracion.builder().id(1L).puntuacion(5).comentario("Muy buena película").customer(customer).movie(movie).build();
        valoracion = valoracionRepository.save(valoracion);
        if (customer.getValoraciones() == null) {
            customer.setValoraciones(new HashSet<>());}
        customer.getValoraciones().add(valoracion);
        if (movie.getValoraciones() == null) {
            movie.setValoraciones(new HashSet<>());}
        movie.getValoraciones().add(valoracion);
        driver.get("http://localhost:8080/customers/" + customer.getId());
        driver.navigate().refresh();
        WebElement valoracionesHeader = driver.findElement(By.id("customer_valoracion"));
        assertEquals("Ver valoraciones usuario", valoracionesHeader.getText());
        WebElement firstValoracion = driver.findElement(By.id("customer_valoracion_movie"));
        assertTrue(firstValoracion.isDisplayed());
        assertEquals("Inception", firstValoracion.getText());
    }
}



