package com.movies.controller;

import com.movies.model.Customer;
import com.movies.repository.CustomerRepository;
import com.movies.repository.MovieRepository;
import com.movies.repository.ValoracionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.server.ResponseStatusException;

@Controller
//@RequestMapping("/customers")
@AllArgsConstructor

public class CustomerController {


    private CustomerRepository customerRepository;
    private MovieRepository movieRepository;
    private ValoracionRepository valoracionRepository;

    @GetMapping("customers")
    public String findAll(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "customer-list";
    }

    @GetMapping("customers/{id}")
    public String findById(@PathVariable("id") Long id, Model model) {
        customerRepository.findById(id)
                .ifPresentOrElse(
                        customer -> model.addAttribute("customer", customer),
                () -> { throw  new IllegalArgumentException("Invalid customer ID:" + id);}
        );
        return "customer-detail";
    }


    @GetMapping("customers/new")
    public String getFormCreateCustomer(Model model) {
        Customer customer = new Customer();
        model.addAttribute("customer", customer);
        return "customer-form";
    }

    @GetMapping("customers/update/{id}")
    public String getFormUpdateCustomer(Model model, @PathVariable Long id) {
        customerRepository.findById(id)
                .ifPresentOrElse(customer-> {
                            model.addAttribute("customer", customer);
                        },
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
                        });
        return "customer-form";
    }

    @PostMapping("customers")
    public String saveCustomer(@ModelAttribute Customer customer) {
        if (customer.getId() == null) {
            customerRepository.save(customer);
        }
        customerRepository.findById(customer.getId())
                .ifPresent(customerDB -> {
                    BeanUtils.copyProperties(customer, customerDB, "id");
                    customerRepository.save(customerDB);
                });

        return "redirect:/customers";
    }


    @GetMapping("customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id) {
        customerRepository.deleteById(id);
        return "redirect:/customers";
    }
}
