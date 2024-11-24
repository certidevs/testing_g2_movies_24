package com.movies.controller.SeleniumTestUI.movieSeleniumTest;

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
public class MovieDetailTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAllInBatch();
        categoriaRepository.deleteAllInBatch();
        valoracionRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    public void testMovieDetailPage() {
        Categoria categoria = categoriaRepository.save(Categoria.builder().nombre("Acción").build());
        Customer customer = customerRepository.save(Customer.builder().nombre("Ana").apellido("C").email("ana.c@example.com").password("123").build());
        Movie movie = movieRepository.save(Movie.builder().id(1L).name("Inception").duration(148).year(2010).categoria(categoria).build());
        Valoracion valoracion = Valoracion.builder().puntuacion(5).comentario("Excelente película").customer(customer).movie(movie).build();
        valoracionRepository.save(valoracion);

        driver.get("http://localhost:8080/movies/" + movie.getId());
        driver.navigate().refresh();

        String header = driver.findElement(By.id("h1_movie_detail")).getText();
        assertEquals("Datos de la Película", header);

        String movieId = driver.findElement(By.id("movie_id")).getText();
        assertEquals( movieId, movie.getId().toString());

        String name = driver.findElement(By.id("name")).getText();
        assertEquals("Inception", name);

        String duration = driver.findElement(By.id("duration")).getText();
        assertEquals("148", duration);

        String year = driver.findElement(By.id("year")).getText();
        assertEquals("2010", year);

        String category = driver.findElement(By.id("movie_category")).getText();
        assertEquals("Acción", category);

        WebElement editButton = driver.findElement(By.id("editBtn_movie"));
        assertTrue(editButton.isDisplayed());
        assertEquals("Editar", editButton.getText());

        WebElement deleteButton = driver.findElement(By.id("deleteBtn_movie"));
        assertTrue(deleteButton.isDisplayed());
        assertEquals("Borrar", deleteButton.getText());

        WebElement backButton = driver.findElement(By.id("backBtn_movie_list"));
        assertTrue(backButton.isDisplayed());
        assertEquals("Volver a la lista de películas", backButton.getText());
    }

    @Test
    public void testMovieValoraciones() {
        Categoria categoria = categoriaRepository.save(Categoria.builder().nombre("Acción").build());
        Customer customer = customerRepository.save(Customer.builder().nombre("Ana").apellido("C").email("ana.c@example.com").password("123").build());
        Movie movie = movieRepository.save(Movie.builder().id(1L).name("Inception").duration(148).year(2010).categoria(categoria).build());
        Valoracion valoracion = Valoracion.builder().puntuacion(5).comentario("Excelente película").customer(customer).movie(movie).build();
        valoracionRepository.save(valoracion);

        driver.get("http://localhost:8080/movies/" + movie.getId());
        driver.navigate().refresh();

        WebElement valoracionesHeader = driver.findElement(By.id("movie_valoracion"));
        assertEquals("Ver valoraciones pelicula", valoracionesHeader.getText());

        WebElement firstValoracionComment = driver.findElement(By.id("movie_valoracion_comentario"));
        assertTrue(firstValoracionComment.isDisplayed());
        assertEquals("Excelente película", firstValoracionComment.getText());
    }
}
