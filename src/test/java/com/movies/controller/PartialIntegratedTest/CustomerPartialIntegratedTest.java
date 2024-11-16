package com.movies.controller.PartialIntegratedTest;

import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerPartialIntegratedTest {

    @Autowired
    private MockMvc mockMvc;

     @MockBean
    private CustomerRepository customerRepository;

     @MockBean
    private MovieRepository movieRepository;

    @MockBean
    private ValoracionRepository valoracionRepository;

    @Test
    void findAll() throws Exception {
        when (customerRepository.findAll()).thenReturn(List.of(
                Customer.builder().id(1L).build(),
        Customer.builder().id(2L).build()));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer-list"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(model().attribute("customers", hasSize(2)));
    }

    @Test
    void findById() throws Exception {
        Customer customer = Customer.builder().id(1L).build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/customers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("customer-detail"))
                .andExpect(model().attributeExists("customer"));
    }

    @Test
    void findById_CustomerNotFound() throws Exception{
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customers/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_IdNotFound() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customer404/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NoSuchElementException))
                .andExpect(result -> assertEquals("Customer not found", result.getResolvedException().getMessage()));

        verify(customerRepository).findById(1L);
    }

    @Test
    void getFormCreateCustomer() throws Exception {
        mockMvc.perform(post("/customers")
                .param("nombre", "Cliente")
                .param("apellido", "1")
                .param("email", "123@gmail.com")
                .param("phone", "123"))
        .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"));

        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void getFormUpdateCustomer() throws Exception {
        Customer customer = Customer.builder().id(1L).build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/customers/update/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("customer-form"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attribute("customer", customer));
    }

     @Test
     void getFormUpdateCustomer_NotFound() throws Exception{
         when(customerRepository.findById(1L)).thenReturn(Optional.empty());

         mockMvc.perform(get("/customers/update/{id}", 1L))
                 .andExpect(status().isNotFound());
     }
    @Test
    void saveCustomerNew () throws Exception {
        mockMvc.perform(post("/customers")
                        .param("nombre", "Cliente")
                        .param("apellido", "1")
                        .param("email", "123@gmail.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"));

        verify(customerRepository).save(any(Customer.class));
    }
    @Test
    void saveCustomerUpdate() throws Exception {
        Customer customer = Customer.builder().id(1L).nombre("Cliente").build();
        when(customerRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(post("/customers/update/{id}", 1L)
                        .param("nombre", "Cliente Actualizado")
                        .param("apellido", "1")
                        .param("email", "123@gmail.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"));

        verify(customerRepository).existsById(1L);
        verify(customerRepository).save(any(Customer.class));
    }
    @Test
    void deleteCustomer() throws Exception{
        mockMvc.perform(post("/customers/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"));

        verify(customerRepository).deleteById(1L);
    }
    @Test
    void addMovieToCustomer() throws Exception {
        Customer customer = Customer.builder().id(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(post("/customers/1/add-movie")
                .param("id", "1")
                .param("nombre", "Pelicula")
                .param("duracion", "60")
                .param("year", "2021"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/1"));

        verify(customerRepository).findById(1L);
        verify(movieRepository).save(any(Movie.class));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void removeMovieFromCustomer() throws Exception {
        mockMvc.perform(post("/customers/1/remove-movie/{movieId}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/1"));

        verify(movieRepository).deleteById(1L);
    }

    @Test
    void addValoracionToCustomer() throws Exception {
        Customer customer = Customer.builder().id(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(post("/customers/1/add-valoracion")
                        .param("rating", "5")
                        .param("comment", "Buen servicio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/1"));

        verify(customerRepository).findById(1L);
        verify(valoracionRepository).save(any(Valoracion.class));
    }
    @Test
    void removeValoracionFromCustomer() throws Exception {
        mockMvc.perform(post("/customers/1/remove-valoracion/{valoracionId}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/1"));

        verify(valoracionRepository).deleteById(1);
    }
}
