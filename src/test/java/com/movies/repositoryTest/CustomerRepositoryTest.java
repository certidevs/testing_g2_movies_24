package com.movies.repositoryTest;

import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.RentalRepository;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Test
    @DisplayName("Prueba del método findByRentedMovie - Clientes encontrados")
    void testFindByRentedMovie() {
        Long movieId = 1L;
        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setName("Película Test");

        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setNombre("Cliente 1");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setNombre("Cliente 2");

        when(customerRepository.findByRentedMovie(movieId)).thenReturn(Arrays.asList(customer1, customer2));

        List<Customer> result = customerRepository.findByRentedMovie(movieId);

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(2, result.size(), "Debería haber 2 clientes en la lista.");
        assertTrue(result.contains(customer1), "La lista debería contener al Cliente 1.");
        assertTrue(result.contains(customer2), "La lista debería contener al Cliente 2.");

        verify(customerRepository).findByRentedMovie(movieId);
    }
    @Test
    @DisplayName("Prueba del método findByNombre - Clientes encontrados")
    void testFindByNombre() {
        String nombre = "Juan";
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setNombre(nombre);

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setNombre(nombre);

        when(customerRepository.findByNombre(nombre)).thenReturn(Arrays.asList(customer1, customer2));

        List<Customer> result = customerRepository.findByNombre(nombre);

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(2, result.size(), "Debería haber 2 clientes en la lista.");
        assertTrue(result.stream().allMatch(c -> c.getNombre().equals(nombre)), "Todos los clientes deberían llamarse Juan.");

        verify(customerRepository).findByNombre(nombre);
    }
    @Test
    @DisplayName("Prueba del método findByValoracionDePelicula - Clientes encontrados")
    void testFindByValoracionDePelicula() {
        Long movieId = 1L;
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setNombre("Cliente 1");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setNombre("Cliente 2");

        when(customerRepository.findByValoracionDePelicula(movieId)).thenReturn(Arrays.asList(customer1, customer2));

        List<Customer> result = customerRepository.findByValoracionDePelicula(movieId);

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(2, result.size(), "Debería haber 2 clientes en la lista.");
        assertTrue(result.contains(customer1), "La lista debería contener al Cliente 1.");
        assertTrue(result.contains(customer2), "La lista debería contener al Cliente 2.");

        verify(customerRepository).findByValoracionDePelicula(movieId);
    }
    @Test
    @DisplayName("Prueba del método findByRentalCountGreaterThan - Clientes encontrados")
    void testFindByRentalCountGreaterThan() {
        int rentalCount = 3;
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setNombre("Cliente 1");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setNombre("Cliente 2");

        when(customerRepository.findByRentalCountGreaterThan(rentalCount)).thenReturn(Arrays.asList(customer1, customer2));

        List<Customer> result = customerRepository.findByRentalCountGreaterThan(rentalCount);

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(2, result.size(), "Debería haber 2 clientes en la lista.");
        assertTrue(result.contains(customer1), "La lista debería contener al Cliente 1.");
        assertTrue(result.contains(customer2), "La lista debería contener al Cliente 2.");

        verify(customerRepository).findByRentalCountGreaterThan(rentalCount);
    }
    @Test
    @DisplayName("Prueba del método findByRentalsInDateRange - Clientes encontrados")
    void testFindByRentalsInDateRange() {
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setNombre("Cliente 1");

        when(customerRepository.findByRentalsInDateRange(startDate, endDate)).thenReturn(Arrays.asList(customer1));

        List<Customer> result = customerRepository.findByRentalsInDateRange(startDate, endDate);

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(1, result.size(), "Debería haber 1 cliente en la lista.");
        assertEquals(customer1, result.get(0), "El cliente en la lista debería coincidir con el de prueba.");

        verify(customerRepository).findByRentalsInDateRange(startDate, endDate);
    }
    @Test
    @DisplayName("Prueba del método findByRentedAndRatedMovies - Clientes encontrados")
    void testFindByRentedAndRatedMovies() {
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setNombre("Cliente 1");

        when(customerRepository.findByRentedAndRatedMovies()).thenReturn(Arrays.asList(customer1));

        List<Customer> result = customerRepository.findByRentedAndRatedMovies();

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(1, result.size(), "Debería haber 1 cliente en la lista.");
        assertEquals(customer1, result.get(0), "El cliente en la lista debería coincidir con el de prueba.");

        verify(customerRepository).findByRentedAndRatedMovies();
    }




}
