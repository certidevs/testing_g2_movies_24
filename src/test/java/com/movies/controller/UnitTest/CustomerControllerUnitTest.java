package com.movies.controller.UnitTest;

import com.movies.controller.CustomerController;
import com.movies.model.Categoria;
import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.CategoriaRepository;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class CustomerControllerUnitTest {
    @InjectMocks
    private CustomerController customerController;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ValoracionRepository valoracionRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    Model model;

    @Test
    void findAll(){
        when (customerRepository.findAll()).thenReturn(List.of(
                Customer.builder().id(1L).build()));
        String view = customerController.findAll(model);
        verify(customerRepository).findAll();
        assertEquals("customer-list", view);
    }

    @Test
    void findById(){
        Customer customer = Customer.builder().id(1L).build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        String view = customerController.findById(1L, model);
        assertEquals("customer-detail", view);
        verify(customerRepository).findById(1L);
        verify(model).addAttribute("customer", customer);
    }
    @Test
    void findById_CustomerNotFound(){
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerController.findById(1L, model);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
        verify(customerRepository).findById(1L);
        verify(model, never()).addAttribute(eq("customer"), any());
    }
    @Test
    void findById_IdNotFound(){
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerController.findById_NotExist(1L, model);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
        verify(customerRepository).findById(1L);
        verify(model, never()).addAttribute(eq("customer"), any());
    }

    @Test
    void getFormCreateCustomer(){
        Customer customer = new Customer();
        String view = customerController.getFormCreateCustomer(model);
        assertEquals("customer-form", view);
        verify(model).addAttribute(eq("customer"), any(Customer.class));
    }

    @Test
    void getFormUpdateCustomer(){
        Customer customer = Customer.builder().id(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        String view = customerController.getFormUpdateCustomer(model, 1L);
        assertEquals("customer-form", view);
        verify(model).addAttribute("customer", customer);
        verify(customerRepository).findById(1L);
    }
    @Test
    void getFormUpdateCustomer_NotFound(){
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerController.getFormUpdateCustomer(model, 1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("not found", exception.getReason());
        verify(customerRepository).findById(1L);
        verify(model, never()).addAttribute(eq("customer"), any());
    }

    @Test
    void saveCustomerNew(){
        Customer customer = new Customer();
        String view = customerController.saveCustomer(customer);
        assertEquals("redirect:/customers", view);
        verify(customerRepository).save(customer);
    }

    @Test
    void saveCustomerUpdate(){
        Customer customer = Customer.builder().id(1L).build();
        Customer customerUpdate = Customer.builder().id(1L).nombre("Editado").build();
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        String view = customerController.saveCustomer(customerUpdate);
        assertEquals("redirect:/customers", view);
        verify(customerRepository).findById(1L);
        verify(customerRepository).existsById(1L);
        verify(customerRepository).save(customer);
        assertEquals(customerUpdate.getNombre(), customer.getNombre());
    }

    @Test
    void deleteCustomer(){
        when(customerRepository.existsById(1L)).thenReturn(true);
        String view = customerController.deleteCustomer(1L);
        assertEquals("redirect:/customers", view);
        verify(customerRepository).deleteById(1L);
    }

  /*  @Test
    void addMovieToCustomer(){
        Customer customer = Customer.builder().id(1L).movies(new HashSet<>()).build();
        //Movie movie = Movie.builder().id(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        List<Long> ids = List.of(1L, 2L);
        List<Movie> movies = List.of(
                Movie.builder().id(1L).build(),
                Movie.builder().id(2L).build()
        );
        when(movieRepository.findAllById(ids)).thenReturn(movies);

        String view = customerController.addMovieToCustomer(1L, customer, ids);
        assertEquals("redirect:/customers/1", view);
        verify(customerRepository).findById(1L);
        verify(movieRepository).findAllById(ids);
        verify(customerRepository).save(any(Customer.class));
    }
    @Test
    void removeMovieFromCustomer(){
        Customer customer = Customer.builder().id(1L).build();
        Movie movie = Movie.builder().id(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        String view = customerController.removeMovieFromCustomer(1L, 1L);
        assertEquals("redirect:/customers/1", view);
        verify(movieRepository).findById(1L);
        verify(customerRepository).save(customer);
        assertFalse(customer.getMovies().contains(movie));
    }
    @Test
    void addValoracionToCustomer(){
        Customer customer = Customer.builder().id(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        String view = customerController.addValoracionToCustomer(1L, 5, "Buen servicio", model);
        assertEquals("redirect:/customers/1", view);
        verify(customerRepository).findById(1L);
        verify(model).addAttribute("customer", customer);
        verify(valoracionRepository).save(any(Valoracion.class));
    }
    @Test
    void removeValoracionFromCustomer(){
        Customer customer = Customer.builder().id(1L).valoraciones(new ArrayList<>()).build();
        Valoracion valoracion = Valoracion.builder().id(1L).build();
        customer.getValoraciones().add(valoracion);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(valoracionRepository.findById(1L)).thenReturn(Optional.of(valoracion));
        String view = customerController.removeValoracionFromCustomer(1L, 1L);
        assertEquals("redirect:/customers/1", view);
        verify(customerRepository).findById(1L);
        verify(valoracionRepository).findById(1L);
        verify(customerRepository).save(customer);
        assertFalse(customer.getValoraciones().contains(valoracion));
    }*/

}
