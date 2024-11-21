package com.movies.controller;

import com.movies.model.Categoria;
import com.movies.model.Movie;
import com.movies.repository.CategoriaRepository;
import com.movies.repository.MovieRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RequiredArgsConstructor
@Controller
//@Valid
//@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;
    private final CategoriaRepository categoriaRepository;



    @GetMapping("movies")
    public String findAll(Model model) {
        model.addAttribute("movies", movieRepository.findAll());
        return "movie-list";
    }

    @GetMapping("movies/{id}")
    public String findById(Model model, @PathVariable Long id) {
        movieRepository.findById(id)
                .ifPresentOrElse(
                        movie -> {
                            model.addAttribute("movie", movie);
                            List<Categoria> categorias = categoriaRepository.findAll();
                            model.addAttribute("categorias", categorias);
                        },
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "movie not found");
                        }
                );
        return "movie-detail";
    }

    @GetMapping("movies/new")
    public String createForm(Model model) {
        Movie movie = new Movie();
        List <Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute( "movie", movie);
       model.addAttribute("categorias", categorias);
        //TODO AÑADIR CATEGORIAS AL CREATE FORM: PRIMERO HACER QUE FUNCIONE EL CREATE FORM
        return "movie-form";
    }

    @GetMapping("movies/update/{id}")
    public String editForm(Model model, @PathVariable Long id) {
        movieRepository.findById(id)
                .ifPresentOrElse(movie -> {
                        model.addAttribute("movie", movie);
    },
            () -> {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
    });
        return "movie-form";

    }

    @PostMapping("movies")
    public String saveMovie(@ModelAttribute Movie movie) {
       if (movie.getId() == null) {
           movieRepository.save(movie);
       }else {
           if (movieRepository.existsById(movie.getId())) {
               movieRepository.findById(movie.getId())
                       .ifPresent(movieDB -> {
                           BeanUtils.copyProperties(movie, movieDB);
                           movieRepository.save(movieDB);
                       });
           }
       }
            return "redirect:/movies";
    }

    @GetMapping("movies/delete/{id}")
    public String deleteById(@PathVariable Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        movieRepository.deleteById(id);
        return "redirect:/movies";
    }
    @PostMapping("movies/{movieId}/add-categoria")
    public String addCategoriaToMovie(@PathVariable Long movieId, @ModelAttribute Movie movie, @RequestParam Long categoriaId) {
        movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        movie.setCategoria(categoria);
        movieRepository.save(movie);
        return "redirect:/movies/"+movieId;
    }
}//