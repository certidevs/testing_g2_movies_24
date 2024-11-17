package com.movies.controller;

import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
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

import java.util.HashSet;
import java.util.NoSuchElementException;

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

    @GetMapping("customer404/{id}")
    public String findById_NotExist(@PathVariable Long id, Model model){
        return customerRepository.findById(id)
                .map(customer -> {
                    model.addAttribute("customer", customer);
                    return "customer-detail";
                })
                .orElseThrow(() ->new NoSuchElementException( "Customer not found"));

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
        }else {
            if (customerRepository.existsById(customer.getId())) {
                customerRepository.findById(customer.getId())
                        .ifPresent(customerDB -> {
                            BeanUtils.copyProperties(customer, customerDB, "id");
                            customerRepository.save(customerDB);
                        });
            }
        }
        return "redirect:/customers";
    }

    @GetMapping("customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id) {
        customerRepository.deleteById(id);
        return "redirect:/customers";
    }
    @PostMapping("customers/{customerId}/add-movie")
    public String addMovieToCustomer(@PathVariable Long customerId,@RequestParam Long id,@RequestParam String nombre,@RequestParam int duracion, @RequestParam int year) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        if (customer.getMovies() == null) {
            customer.setMovies(new HashSet<>());
        }
        Movie movie = new Movie();
        movie.setId(id);
        movie.setName(nombre);
        movie.setDuration(duracion);
        movie.setYear(year);
        customer.getMovies().add(movie);
        movieRepository.save(movie);
        return "redirect:/customers" ;
    }


    @PostMapping("customers/{customerId}/remove-movie/{movieId}")
    public String removeMovieFromCustomer(@PathVariable Long customerId, @PathVariable Long movieId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        if (customer.getMovies() == null) {
            customer.setMovies(new HashSet<>());
        }
        customer.getMovies().remove(movie);
        customerRepository.save(customer);

        return "redirect:/customers/" + customerId;
    }
    @PostMapping("customers/{customerId}/add-valoracion")
    public String addValoracionToCustomer(@PathVariable Long customerId, @RequestParam int puntuacion, @RequestParam String comentario, Model model) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        Valoracion valoracion = new Valoracion();
        valoracion.setPuntuacion(puntuacion);
        valoracion.setComentario(comentario);
        valoracion.setCustomer(customer);
        valoracionRepository.save(valoracion);
        model.addAttribute("customer", customer);
        return "redirect:/customers/" + customerId;
    }
    @PostMapping("customers/{customerId}/remove-valoracion/{valoracionId}")
    public String removeValoracionFromCustomer(@PathVariable Long customerId, @PathVariable int valoracionId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        Valoracion valoracion = valoracionRepository.findById(valoracionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Valoracion not found"));

        customer.getValoraciones().remove(valoracion);
        //valoracionRepository.delete(valoracion);
        customerRepository.save(customer);

        return "redirect:/customers/" + customerId;
    }




}
