package com.movies.controller.UnitTest;

import com.movies.controller.CustomerController;
import com.movies.model.Customer;
import com.movies.model.Valoracion;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String view = customerController.findById(1L, model);
        assertEquals("customer-detail", view);
        verify(customerRepository).findById(1L);
        verify(model, never()).addAttribute(eq("customer"), any());
    }

    @Test
    void getFormCreateCustomer(){
        List<Customer> customers = List.of(
                Customer.builder().id(1L).build(),
                Customer.builder().id(2L).build()
        );
        when(customerRepository.findAll()).thenReturn(customers);
        String view = customerController.getFormCreateCustomer(model);
        assertEquals("customer-form", view);
        verify(model).addAttribute("customer", customers);
        verify(model).addAttribute(eq("customers"), any());
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
        List<Customer> customers = List.of(
                Customer.builder().id(1L).build(),
                Customer.builder().id(2L).build()
        );
        when(customerRepository.findAll()).thenReturn(customers);
        String view = customerController.getFormUpdateCustomer(model, 1L);
        assertEquals("customer-form", view);
        verify(model, never()).addAttribute(eq("customer"), any());
        verify(model).addAttribute("customers", customers);
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
        String view = customerController.saveCustomer(customerUpdate);
        assertEquals("redirect:/customers", view);
        verify(customerRepository).findById(1L);
        verify(customerRepository).existsById(1L);
        verify(customerRepository).save(customer);
        assertEquals(customerUpdate.getNombre(), customer.getNombre());
    }

    @Test
    void deleteCustomer(){
        String view = customerController.deleteCustomer(1L);
        assertEquals("redirect:/customers", view);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    void addMovieToCustomer(){
        Customer customer = Customer.builder().id(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        String view = customerController.addMovieToCustomer(1L, 1L, "Pelicula", 60, 2021);
        assertEquals("redirect:/customers", view);
        verify(customerRepository).findById(1L);
        verify(movieRepository).save(any());
    }
    @Test
    void removeMovieFromCustomer(){
        String view = customerController.removeMovieFromCustomer(1L, 1L);
        assertEquals("redirect:/customers", view);
        verify(movieRepository).deleteById(1L);
    }
    @Test
    void addValoracionToCustomer(){
        Customer customer = Customer.builder().id(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        String view = customerController.addValoracionToCustomer(1L, 5, "Buen servicio");
        assertEquals("valoracion-list", view);
        verify(customerRepository).findById(1L);
        verify(model).addAttribute("customer", customer);
        verify(valoracionRepository).save(any(Valoracion.class));
    }
    @Test
    void removeValoracionFromCustomer(){
        String view = customerController.removeValoracionFromCustomer(1L, 1);
        assertEquals("redirect:/customers", view);
        verify(valoracionRepository).deleteById(1);
    }

}
