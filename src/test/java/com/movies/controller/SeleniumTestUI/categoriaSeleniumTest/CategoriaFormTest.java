package com.movies.controller.SeleniumTestUI.categoriaSeleniumTest;

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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CategoriaFormTest {
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    WebDriver driver;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAllInBatch();
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
    public void testPageTitleAndHeader() {
        categoriaRepository.save(Categoria.builder().id(1L).nombre("Categoria").descripcion("Descripcion").build());
        driver.get("http://localhost:8080/categorias/new");
        driver.navigate().refresh();
        String title = driver.getTitle();
        assertEquals("Formulario de Categoria", title);
        String header = driver.findElement(By.id("categoria_form_h1")).getText();
        assertEquals("Formulario Categoria", header);
    }
}

