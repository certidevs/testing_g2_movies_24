package com.movies.controller.IntegrationTest;

import com.movies.model.Customer;
import com.movies.repository.CategoriaRepository;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Test
    void findAll() throws Exception {
        customerRepository.save(Customer.builder().id(1L).build());

        mockMvc.perform(get("/customers"))
               .andExpect(status().isOk())
               .andExpect(view().name("customer-list"))
               .andExpect(model().attributeExists("customers"));
    }
    @Test
    void findById() throws Exception {
        Customer customer = customerRepository.save(Customer.builder().id(1L).build());

        mockMvc.perform(get("/customers/{id}", customer.getId()))
               .andExpect(status().isOk())
               .andExpect(view().name("customer-detail"))
               .andExpect(model().attributeExists("customer"));
    }
    @Test
    void findById_NotExist() throws Exception {
        mockMvc.perform(get("/customers404/{id}", 1L))
               .andExpect(status().isNotFound());
    }
    @Test
    void getFormToCreateCustomer() throws Exception {
        mockMvc.perform(get("/customers/new"))
               .andExpect(status().isOk())
               .andExpect(view().name("customer-form"))
               .andExpect(model().attributeExists("customer"));
    }
    @Test
    void getFormToUpdateCustomer() throws Exception {
        Customer customer = customerRepository.save(Customer.builder().id(1L).build());

        mockMvc.perform(get("/customers/update/{id}", customer.getId()))
               .andExpect(status().isOk())
               .andExpect(view().name("customer-form"))
               .andExpect(model().attributeExists("customer"));
    }
    @Test
    void saveCustomer() throws Exception {
        mockMvc.perform(get("/customers"))
               .andExpect(status().isOk())
               .andExpect(view().name("customer-list"))
               .andExpect(model().attributeExists("customers"));
    }
    @Test
    void deleteCustomer() throws Exception {
        Customer customer = customerRepository.save(Customer.builder().id(1L).build());

        mockMvc.perform(get("/customers/delete/{id}", customer.getId()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/customers"));
    }

}
