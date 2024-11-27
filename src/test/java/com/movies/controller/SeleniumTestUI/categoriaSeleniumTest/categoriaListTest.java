package com.movies.controller.SeleniumTestUI.categoriaSeleniumTest;

import com.movies.model.Categoria;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class categoriaListTest {
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
        customerRepository.deleteAllInBatch();
        valoracionRepository.deleteAllInBatch();
        driver = new ChromeDriver();
    }
    @AfterEach
    void tearDown() {
        driver.quit();
    }
    @Test
    public void testCategoriaListPageTitleAndHeader() {
        categoriaRepository.save(Categoria.builder().id(1L).nombre("Categoria").descripcion("Descripcion").build());
        driver.get("http://localhost:8080/categorias");
        driver.navigate().refresh();
        String title =driver.getTitle();
        assertEquals("Lista de Categorias", title);
        String header = driver.findElement(By.id("h1_categoria_list")).getText();
        assertEquals("Lista de Categorias", header);
    }

    @Test
    public void testCreateNewCategoriaButton() {
        categoriaRepository.save(Categoria.builder().id(1L).nombre("Categoria").descripcion("Descripcion").build());
        driver.get("http://localhost:8080/categorias");
        driver.navigate().refresh();
        WebElement createButton = driver.findElement(By.id("btnCreate_categoria"));
        assertTrue(createButton.isDisplayed());
        assertEquals("Nuevo Cliente", createButton.getText());

        String createButtonHref = createButton.getAttribute("href");
        assertTrue(createButtonHref.endsWith("/categorias/new"));
    }

    @Test
    public void testCategoriaTableContent() {
        categoriaRepository.save(Categoria.builder().id(1L).nombre("Categoria").descripcion("Descripcion").build());
        driver.get("http://localhost:8080/categorias");
        driver.navigate().refresh();
        WebElement categoriaTable = driver.findElement(By.id("categoria_list_table"));
        assertTrue(categoriaTable.isDisplayed());

        List<WebElement> headers = categoriaTable.findElements(By.tagName("th"));
        assertEquals(3, headers.size());
        assertEquals("id", headers.get(0).getText());
        assertEquals("Nombre", headers.get(1).getText());
        assertEquals("Descripcion", headers.get(2).getText());

        List<WebElement> rows = categoriaTable.findElements(By.tagName("tr"));
        assertTrue(rows.size() > 1);
    }

    @Test
    public void testCategoriaActions() {
        categoriaRepository.save(Categoria.builder().id(1L).nombre("Ana").descripcion("Descripcion").build());
        driver.get("http://localhost:8080/categorias");
        driver.navigate().refresh();
        List<WebElement> actionButtons = driver.findElements(By.cssSelector("td a"));

        for (WebElement button : actionButtons) {
            String buttonId = button.getAttribute("id");
            assertTrue(buttonId.equals("categoria_view") || buttonId.equals("categoria_update") || buttonId.equals("categoria_delete"));
            assertTrue(button.isDisplayed());
        }

        WebElement viewButton = driver.findElement(By.id("categoria_view"));
        String viewHref = viewButton.getAttribute("href");
        assertTrue(viewHref.contains("/categorias/"));

        WebElement editButton = driver.findElement(By.id("categoria_update"));
        String editHref = editButton.getAttribute("href");
        assertTrue(editHref.contains("/categorias/update/"));

        WebElement deleteButton = driver.findElement(By.id("categoria_delete"));
        String deleteHref = deleteButton.getAttribute("href");
        assertTrue(deleteHref.contains("/categorias/delete/"));
    }

    @Test
    public void testNoCategoriasMessage() {
        categoriaRepository.save(Categoria.builder().id(1L).nombre("Categoria").descripcion("Descripcion").build());
        driver.get("http://localhost:8080/categorias");
        driver.navigate().refresh();
        List<WebElement> noCategoriasMessage = driver.findElements(By.id("noCategorias"));
        if (!noCategoriasMessage.isEmpty()) {
            assertTrue(noCategoriasMessage.get(0).isDisplayed());
            assertEquals("No hay clientes.", noCategoriasMessage.get(0).getText());
        }
    }
}
