package com.movies.controller.SeleniumTestUI.movieSeleniumTest;

import com.movies.model.Categoria;
import com.movies.model.Movie;
import com.movies.repository.CategoriaRepository;
import com.movies.repository.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MovieFormTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAllInBatch();
        categoriaRepository.deleteAllInBatch();
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    public void testPageTitleAndHeader() {
        driver.get("http://localhost:8080/movies/new");
        driver.navigate().refresh();
        String title = driver.getTitle();
        assertEquals("Formulario de Película", title);
        String header = driver.findElement(By.id("movie_form_h1")).getText();
        assertEquals("Formulario peliculas", header);
    }

    @Test
    public void testFillAndSubmitFormForNewMovie() {
        Categoria categoria = categoriaRepository.save(Categoria.builder().id(1L).nombre("Acción").build());

        driver.get("http://localhost:8080/movies/new");
        driver.navigate().refresh();
        driver.findElement(By.id("name")).sendKeys("Inception");
        driver.findElement(By.id("duration")).sendKeys("148");
        driver.findElement(By.id("year")).sendKeys("2010");

        WebElement categorySelect = driver.findElement(By.id("movie_categoria"));
        categorySelect.findElement(By.xpath("//option[text()='Acción']")).click();

        driver.findElement(By.id("movie_save_new")).click();

    }

    @Test
    public void testFillAndSubmitFormForExistingMovie() {
        Categoria categoria = categoriaRepository.save(Categoria.builder().id(1L).nombre("Acción").build());

        Movie movie = movieRepository.save(Movie.builder().id(1L).name("Matrix").duration(136).year(1999).categoria(categoria).build());

        driver.get("http://localhost:8080/movies/update/" + movie.getId());
        driver.navigate().refresh();
        WebElement nameInput = driver.findElement(By.id("name"));
        nameInput.clear();
        nameInput.sendKeys("The Matrix");

        WebElement durationInput = driver.findElement(By.id("duration"));
        durationInput.clear();
        durationInput.sendKeys("150");

        WebElement yearInput = driver.findElement(By.id("year"));
        yearInput.clear();
        yearInput.sendKeys("2000");

        driver.findElement(By.id("movie_save_update")).click();

    }

    @Test
    public void testBackButton() {
        driver.get("http://localhost:8080/movies/new");
        driver.navigate().refresh();
        WebElement backButton = driver.findElement(By.id("backBtn_movies_list"));
        assertTrue(backButton.isDisplayed());
        assertEquals("Cancelar", backButton.getText());

        backButton.click();
        assertEquals("http://localhost:8080/movies", driver.getCurrentUrl());
    }
}

