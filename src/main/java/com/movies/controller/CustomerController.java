package com.movies.controller;

import com.movies.model.Customer;
import com.movies.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.repository.CrudRepository;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "customer-list";
    }

    @GetMapping("/new")
    public String createCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-form";
    }

    @PostMapping
    public String saveCustomer(@ModelAttribute("customer") Customer customer) {
        customerRepository.save(customer);
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID:" + id));
        model.addAttribute("customer", customer);
        return "customer-form";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable("id") int id, @ModelAttribute("customer") Customer customer) {
        customer.setId(id);
        customerRepository.save(customer);
        return "redirect:/customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") int id) {
        customerRepository.deleteById(id);
        return "redirect:/customers";
    }
}
