package com.movies.controller.PartialIntegratedTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movies.model.Customer;
import com.movies.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerApiPartialIntegratedTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setNombre("Juan");
        customer.setApellido("Pérez");
        customer.setEmail("juan@example.com");
    }

    @Test
    void testWelcome() throws Exception {
        mockMvc.perform(get("/api/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().string("Bienvenido a un controlador de Spring"));
    }

    @Test
    void testGetUserName() throws Exception {
        mockMvc.perform(get("/api/user?name=Daniela"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome user Daniela"));
    }

    @Test
    void testFindAllCustomers() throws Exception {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    void testFindCustomerById() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void testFindCustomerByIdNotFound() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCustomer() throws Exception {
        Customer newCustomer = new Customer();
        newCustomer.setNombre("Ana");
        newCustomer.setApellido("Lopez");
        newCustomer.setEmail("ana@example.com");

        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        mockMvc.perform(post("/api/customers/new")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(newCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void testCreateCustomerBadRequest() throws Exception {
        customer.setId(1L);  // Simulando que ya tiene un ID, lo cual es un error.
        when(customerRepository.save(any(Customer.class))).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/customers/new")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(customer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCustomer() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customer.setNombre("Carlos");

        mockMvc.perform(put("/api/customers/update")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos"));
    }

    @Test
    void testDeleteCustomer() throws Exception {
        doNothing().when(customerRepository).deleteById(1L);

        mockMvc.perform(delete("/api/customers/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCustomerNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT)).when(customerRepository).deleteById(1L);

        mockMvc.perform(delete("/api/customers/delete/1"))
                .andExpect(status().isConflict());
    }
}
