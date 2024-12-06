package com.movies.ServiceTest;

import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Rental;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.RentalRepository;
import com.movies.service.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private RentalService rentalService;

    private Movie movie;
    private Customer customer;
    private Rental rental;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setName("Película Test");
        movie.setRentalPricePerDay(10.0);
        movie.setAvailable(true);

        customer = new Customer();
        customer.setId(1L);
        customer.setNombre("Cliente Test");
        customer.setEmail("test@example.com");

        rental = new Rental();
        rental.setId(1L);
        rental.setMovie(movie);
        rental.setCustomer(customer);
        rental.setRentalDate(LocalDateTime.now());
        rental.setReturnDueDate(LocalDateTime.now().plusDays(5));
        rental.setRentalPrice(50.0);
    }

    @Test
    @DisplayName("Prueba del método rentMovie - Alquiler exitoso")
    void testRentMovie_Success() {
        // Simula la búsqueda de una película por ID en el repositorio de películas.
        // Mockito devuelve el objeto `movie` cuando se llama al método `findById` con el ID de la película.
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));

        // Simula la búsqueda de un cliente por ID en el repositorio de clientes.
        // Mockito devuelve el objeto `customer` cuando se llama al método `findById` con el ID del cliente.
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        // Simula el guardado de un alquiler en el repositorio de alquileres.
        // Mockito devuelve el mismo objeto `Rental` que fue pasado al método `save`.
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Llamada al método del servicio que se está probando.
        // Se realiza un alquiler para el cliente con el ID de la película y la duración del alquiler en días.
        Rental result = rentalService.rentMovie(customer.getId(), movie.getId(), 5);

        // Validaciones del resultado:

        // Asegurarse de que el alquiler no sea nulo.
        assertNotNull(result, "El alquiler no debería ser nulo.");

        // Validar que la película asociada al alquiler es la correcta.
        assertEquals(movie, result.getMovie(), "La película del alquiler debería coincidir.");

        // Validar que el cliente asociado al alquiler es el correcto.
        assertEquals(customer, result.getCustomer(), "El cliente del alquiler debería coincidir.");

        // Comprobar que el precio del alquiler fue calculado correctamente (5 días x 10.0 por día).
        assertEquals(50.0, result.getRentalPrice(), "El precio del alquiler debería ser 50.0.");

        // Verificar que la fecha de alquiler no sea nula.
        assertNotNull(result.getRentalDate(), "La fecha del alquiler no debería ser nula.");

        // Validar que la fecha de devolución es 5 días después de la fecha actual.
        assertEquals(LocalDateTime.now().plusDays(5).getDayOfMonth(),
                result.getReturnDueDate().getDayOfMonth(),
                "La fecha de devolución debería ser en 5 días.");

        // Verificaciones de las interacciones con los repositorios:

        // Asegurarse de que se llamó al método `findById` del repositorio de películas.
        verify(movieRepository).findById(movie.getId());

        // Asegurarse de que se llamó al método `findById` del repositorio de clientes.
        verify(customerRepository).findById(customer.getId());

        // Verificar que la película fue guardada en el repositorio después de actualizar su estado.
        verify(movieRepository).save(movie);

        // Asegurarse de que el alquiler fue guardado en el repositorio.
        verify(rentalRepository).save(any(Rental.class));
    }


    @Test
    @DisplayName("Prueba del método rentMovie - Película no disponible")
    void testRentMovie_MovieNotAvailable() {
        movie.setAvailable(false);
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rentalService.rentMovie(customer.getId(), movie.getId(), 5);
        }, "Se debería lanzar una excepción cuando la película no está disponible.");

        assertEquals("La película no está disponible.", exception.getMessage(), "El mensaje de la excepción debería ser 'La película no está disponible.'");

        verify(movieRepository).findById(movie.getId());
        verify(customerRepository, never()).findById(anyLong());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    @DisplayName("Prueba del método returnMovie - Devolución exitosa")
    void testReturnMovie_Success() {
        movie.setAvailable(false);
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        rentalService.returnMovie(rental.getId());

        assertTrue(movie.isAvailable(), "La película debería estar disponible después de la devolución.");

        verify(rentalRepository).findById(rental.getId());
        verify(movieRepository).save(movie);
        verify(rentalRepository).save(rental);
    }

    @Test
    @DisplayName("Prueba del método returnMovie - Alquiler no encontrado")
    void testReturnMovie_NotFound() {
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rentalService.returnMovie(rental.getId());
        }, "Se debería lanzar una excepción cuando el alquiler no es encontrado.");

        assertEquals("Alquiler no encontrado.", exception.getMessage(), "El mensaje de la excepción debería ser 'Alquiler no encontrado.'");

        verify(rentalRepository).findById(rental.getId());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    @DisplayName("Prueba del método getRentalsByCustomer")
    void testGetRentalsByCustomer() {
        when(rentalRepository.findByCustomerId(customer.getId())).thenReturn(Arrays.asList(rental));

        List<Rental> result = rentalService.getRentalsByCustomer(customer.getId());

        assertNotNull(result, "La lista de alquileres no debería ser nula.");
        assertEquals(1, result.size(), "Debería haber un alquiler en la lista.");
        assertEquals(rental, result.get(0), "El alquiler de la lista debería coincidir con el de prueba.");

        verify(rentalRepository).findByCustomerId(customer.getId());
    }

    @Test
    @DisplayName("Prueba del método getRentalsByMovie")
    void testGetRentalsByMovie() {

        when(rentalRepository.findByMovieId(movie.getId())).thenReturn(Arrays.asList(rental));

        List<Rental> result = rentalService.getRentalsByMovie(movie.getId());

        assertNotNull(result, "La lista de alquileres no debería ser nula.");
        assertEquals(1, result.size(), "Debería haber un alquiler en la lista.");
        assertEquals(rental, result.get(0), "El alquiler de la lista debería coincidir con el de prueba.");

        verify(rentalRepository).findByMovieId(movie.getId());
    }
}
