package com.movies.controller;

import com.movies.model.Categoria;
import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.CategoriaRepository;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
//@RequestMapping("/customers")
@AllArgsConstructor

public class CustomerController {


    private CustomerRepository customerRepository;
    private MovieRepository movieRepository;
    private ValoracionRepository valoracionRepository;
    private CategoriaRepository categoriaRepository;


    @GetMapping("customers")
    public String findAll(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "customer-list";
    }
    //TODO: Bug-> ver si la bbdd sigue borrando los datos de las tablas cliente-CAMBIADO A UPDATE APP PROPERTIES

    @GetMapping("customers/{id}")
    public String findById(@PathVariable("id") Long id, Model model) {
        customerRepository.findById(id)
                .ifPresentOrElse(
                        customer -> {
                        model.addAttribute("customer", customer);
                        List<Categoria> categorias = categoriaRepository.findAll();
                        model.addAttribute("categorias", categorias);
                        },
                        () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"); }
        );
        return "customer-detail";
    }

    @GetMapping("customers404/{id}")
    public String findById_NotExist(@PathVariable Long id, Model model){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        model.addAttribute("customer", customer);
         return "customer-detail";
    }


    @GetMapping("customers/new")
    public String getFormCreateCustomer(Model model) {
        Customer customer = new Customer();
        List<Movie> movies = movieRepository.findAll();
        model.addAttribute("movies", movies);
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
        if (!customerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        customerRepository.deleteById(id);
        return "redirect:/customers";
    }
   @PostMapping("customers/{customerId}/add-movie")
    public String addMovieToCustomer(@PathVariable Long customerId,@RequestParam Long id,@RequestParam String name,@RequestParam int duration, @RequestParam int year, @RequestParam("movies") List<Long> movieIds) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
       List<Movie> movies = movieRepository.findAllById(movieIds);
       customer.getMovies().addAll(movies);
           customerRepository.save(customer);

        return "redirect:/customers/" + customerId;
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
    //TODO: Bug-> ver si funciona
    @PostMapping("customers/{customerId}/remove-valoracion/{valoracionId}")
    public String removeValoracionFromCustomer(@PathVariable Long customerId, @PathVariable Long valoracionId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        Valoracion valoracion = valoracionRepository.findById(valoracionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Valoracion not found"));
        if (customer.getValoraciones() == null) {
            customer.setValoraciones(new ArrayList<>());
        }
        customer.getValoraciones().remove(valoracion);
        customerRepository.save(customer);

        return "redirect:/customers/" + customerId;
    }
    //TODO: Bug-> ver si funciona

}
