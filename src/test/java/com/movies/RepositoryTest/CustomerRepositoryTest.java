package com.movies.RepositoryTest;

import com.movies.model.*;
import com.movies.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

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

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Prueba del método findByRentedMovie ")
    void testFindByRentedMovie() {
        List<Movie> movies = Arrays.asList(
                Movie.builder().id(1L).name("Película 1").year(2024).duration(120).build(),
                Movie.builder().id(2L).name("Película 2").year(2025).duration(60).build()
        );
        movieRepository.saveAll(movies);

        List<Customer> customers = Arrays.asList(
                Customer.builder().id(1L).nombre("Cliente 1").build(),
                Customer.builder().id(2L).nombre("Cliente 2").build()
        );
        customerRepository.saveAll(customers);

        List<Rental> rentals = Arrays.asList(
                Rental.builder().id(1L).movie(movies.get(0)).customer(customers.get(0)).build(),
                Rental.builder().id(2L).movie(movies.get(0)).customer(customers.get(1)).build()
        );
        rentalRepository.saveAll(rentals);

        Long movieId = movies.get(0).getId();
        List<Customer> result = customerRepository.findByRentedMovie(movieId);

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(2, result.size(), "2 clientes han alquilado peliculas");
        assertTrue(result.contains(customers.get(0)), "La lista debería contener al Cliente 1 alquilando la pelicula 1");
        assertTrue(result.contains(customers.get(1)), "La lista debería contener al Cliente 2 alquilando la pelicula2.");
        for (Rental rental : rentals) {
            assertTrue(rental.getMovie().getId().equals(movieId),
                    "El cliente debería haber alquilado la película especificada.");
        }
    }

    @Test
    @DisplayName("Prueba del método findByNombre ")
    void testFindByNombre() {
        List<Customer> customers = Arrays.asList(
                Customer.builder().id(1L).nombre("Juan").build(),
                Customer.builder().id(2L).nombre("Juan").build()
        );
        customerRepository.saveAll(customers);
        List<Customer> result = customerRepository.findByNombre("Juan");

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(2, result.size(), "Debería haber 2 clientes en la lista.");
        assertTrue(result.stream().allMatch(c -> c.getNombre().equals("Juan")), "Todos los clientes deberían llamarse Juan.");
    }

    @Test
    @DisplayName("Prueba del método findByValoracionDePelicula ")
    void testFindByValoracionDePelicula() {
        Categoria categoria = Categoria.builder().nombre("ciencia-espacial").descripcion("marcianitos").build();
        categoriaRepository.save(categoria);

        List<Movie> movies = Arrays.asList(
                Movie.builder().name("Película 1").year(2024).duration(120).categoria(categoria).build(),
                Movie.builder().name("Película 2").year(2025).duration(60).categoria(categoria).build()
        );
        movieRepository.saveAll(movies);

        List<Customer> customers = Arrays.asList(
                Customer.builder().nombre("Cliente 1").build(),
                Customer.builder().nombre("Cliente 2").build()
        );
        customerRepository.saveAll(customers);

        List<Valoracion> valoraciones = Arrays.asList(
                Valoracion.builder().puntuacion(5).comentario("muy buena").movie(movies.get(0)).customer(customers.get(0)).build(),
                Valoracion.builder().puntuacion(4).comentario("buena").movie(movies.get(0)).customer(customers.get(1)).build()
        );
        valoracionRepository.saveAll(valoraciones);

        List<Customer> result = customerRepository.findByValoracionDePelicula(movies.get(0).getId());

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(2, result.size(), "Debería haber 2 clientes en la lista.");
        assertTrue(result.contains(customers.get(0)), "La lista debería contener la valoracion del Cliente 1.");
        assertTrue(result.contains(customers.get(1)), "La lista debería contener la valoracion del Cliente 2.");
        List<Valoracion> valoracionesResult = valoracionRepository.findAll().stream()
                .filter(v -> v.getMovie().getId().equals(movies.get(0).getId()))
                .collect(Collectors.toList());
        assertEquals(2, valoracionesResult.size(), "Debería haber 2 valoraciones para la película 1.");
        assertTrue(valoracionesResult.stream().allMatch(v -> v.getMovie().equals(movies.get(0))), "Todas las valoraciones deberían ser para la película 1.");
    }

    @Test
    @DisplayName("Prueba del método findByRentalCountGreaterThan ")
    void testFindByRentalCountGreaterThan() {
        int rentalCount = 3;

        List<Movie> movies = Arrays.asList(
                Movie.builder().name("Película 1").year(2024).duration(120).build(),
                Movie.builder().name("Película 2").year(2025).duration(60).build()
        );
        movieRepository.saveAll(movies);

        List<Customer> customers = Arrays.asList(
                Customer.builder().nombre("Cliente 1").build(),
                Customer.builder().nombre("Cliente 2").build()
        );
        customerRepository.saveAll(customers);

        List<Rental> rentals = Arrays.asList(
                Rental.builder().movie(movies.get(0)).customer(customers.get(0)).build(),
                Rental.builder().movie(movies.get(0)).customer(customers.get(0)).build(),
                Rental.builder().movie(movies.get(0)).customer(customers.get(0)).build(),
                Rental.builder().movie(movies.get(1)).customer(customers.get(1)).build(),
                Rental.builder().movie(movies.get(0)).customer(customers.get(0)).build()
        );
        rentalRepository.saveAll(rentals);

        List<Customer> result = customerRepository.findByRentalCountGreaterThan(rentalCount);

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(1, result.size(), "Debería haber 1 cliente en la lista.");
        assertTrue(result.contains(customers.get(0)), "La lista debería contener al Cliente 1 alquilando 4 veces.");
        for (Customer customer : result) {
            long rentalCountForCustomer = rentals.stream()
                    .filter(rental -> rental.getCustomer().equals(customer))
                    .count();
            assertTrue(rentalCountForCustomer > rentalCount,
                    "El cliente debería tener más de " + rentalCount + " alquileres.");
        }
    }

    @Test
    @DisplayName("Prueba del método findByRentalsInDateRange ")
    void testFindByRentalsInDateRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse("2024-01-01 00:00:00", formatter);
        LocalDateTime endDate = LocalDateTime.parse("2024-12-31 23:59:59", formatter);

        Customer customer1 = Customer.builder().nombre("Cliente1").build();
        customerRepository.save(customer1);

        Movie movie = Movie.builder().name("Película 1").year(2024).duration(120).build();
        movieRepository.save(movie);

        Rental rental = Rental.builder().customer(customer1).movie(movie).rentalDate(LocalDateTime.parse("2024-06-15 12:00:00", formatter)).build();
        rentalRepository.save(rental);
        List<Customer> result = customerRepository.findByRentalsInDateRange(startDate, endDate);

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(1, result.size(), "Debería haber 1 cliente en la lista.");
        assertEquals(customer1, result.get(0), "El cliente en la lista debería coincidir con el de prueba.");
        List<Rental> rentalsResult = rentalRepository.findAll().stream()
                .filter(r -> r.getCustomer().equals(customer1) &&
                        r.getRentalDate().isAfter(startDate) &&
                        r.getRentalDate().isBefore(endDate))
                .collect(Collectors.toList());
        assertEquals(1, rentalsResult.size(), "Debería haber 1 alquiler en el rango de fechas especificado.");
        assertTrue(rentalsResult.stream().allMatch(r -> r.getRentalDate().isAfter(startDate) && r.getRentalDate().isBefore(endDate)), "Todas las fechas de alquiler deberían estar dentro del rango especificado.");
    }

    @Test
    @DisplayName("Prueba del método findByRentedAndRatedMovies")
    void testFindByRentedAndRatedMovies() {
        List<Customer> customers = customerRepository.saveAll(Arrays.asList(
                Customer.builder().nombre("Cliente 1").build(),
                Customer.builder().nombre("Cliente 2").build()
        ));

        Categoria categoria = categoriaRepository.save(
                Categoria.builder().nombre("ciencia-espacial").descripcion("marcianitos").build()
        );

        List<Movie> movies = movieRepository.saveAll(Arrays.asList(
                Movie.builder().name("Película 1").year(2024).duration(120).categoria(categoria).build(),
                Movie.builder().name("Película 2").year(2025).duration(60).categoria(categoria).build()
        ));

        List<Rental> rentals= rentalRepository.saveAll(Arrays.asList(
                Rental.builder().movie(movies.get(0)).customer(customers.get(0)).build(),
                Rental.builder().movie(movies.get(1)).customer(customers.get(1)).build()
        ));

        List<Valoracion> valoraciones= valoracionRepository.saveAll(Arrays.asList(
                Valoracion.builder().puntuacion(5).comentario("muy buena").movie(movies.get(0)).customer(customers.get(0)).build(),
                Valoracion.builder().puntuacion(4).comentario("buena").movie(movies.get(1)).customer(customers.get(1)).build()
        ));
        customers.get(0).getValoraciones().add(valoraciones.get(0));
        customers.get(1).getValoraciones().add(valoraciones.get(1));
        customers.get(0).getRentals().add(rentals.get(0));
        customers.get(1).getRentals().add(rentals.get(1));

        movies.get(0).getRentals().add(rentals.get(0));
        movies.get(1).getRentals().add(rentals.get(1));

        movies.get(0).getValoraciones().add(valoraciones.get(0));
        movies.get(0).getValoraciones().add(valoraciones.get(1));

        categoriaRepository.save(categoria);
        customerRepository.saveAll(customers);
        movieRepository.saveAll(movies);
        rentalRepository.saveAll(rentals);
        valoracionRepository.saveAll(valoraciones);

        List<Customer> result = customerRepository.findByRentedAndRatedMovies();

        assertNotNull(result, "La lista de clientes no debería ser nula.");
        assertEquals(2, result.size(), "Debería haber 2 clientes en la lista.");
        assertTrue(result.contains(customers.get(0)), "La lista debería contener al Cliente 1.");
        assertTrue(result.contains(customers.get(1)), "La lista debería contener al Cliente 2.");

        for (Customer customer : result) {
            boolean hasRentedAndRated = customer.getRentals().stream()
                    .anyMatch(rental -> customer.getValoraciones().stream()
                            .anyMatch(valoracion -> valoracion.getMovie().equals(rental.getMovie())));

            assertTrue(hasRentedAndRated,
                    "El cliente debería tener al menos una película alquilada y valorada.");
        }
    }

}
