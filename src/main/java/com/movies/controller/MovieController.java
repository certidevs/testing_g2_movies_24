package com.movies.controller;

import com.movies.model.Movie;
import com.movies.repository.MovieRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RequiredArgsConstructor
@Controller
@Valid
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    private final MovieRepository movieRepository;




    @GetMapping("")
    public String findAll(HttpSession session, Model model) {
        model.addAttribute("returnUrl", "movie");
        List<Movie> movies = StreamSupport.stream(movieRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        if (!movies.isEmpty()) {
            model.addAttribute("movies", movies);
            model.addAttribute("entity", "películas");
        } else {
            addErrorMessage(model, "No hay ninguna película que mostrar");
        }

        return "movie-list";
    }

    private void addErrorMessage(Model model, String message) {
        model.addAttribute("error", "\uD83E\uDD74 " + message);
    }

    @GetMapping("/{id}")
    public String findById(Model model, @PathVariable Long id) {
        model.addAttribute("returnUrl", "movie");

        if (id > 0) {
            Optional<Movie> movieOptional = movieRepository.findById(id);
            if (movieOptional.isPresent() ) {
                model.addAttribute("movie", movieOptional.get());
                return "movie-detail.html";
            }
        }
        model.addAttribute("error", "\uD83E\uDD74 Película no encontrada");
        return "movie-list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("returnUrl", "movie");
        return "movie-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(Model model, @PathVariable Long id) {
        model.addAttribute("returnUrl", "movie");

        if (id > 0) {
            Optional<Movie> movieOptional = movieRepository.findById(id);
            if (movieOptional.isPresent()) {
                model.addAttribute("movie", movieOptional.get());
                return "movie-form";
            }
        }
        model.addAttribute("error", "\uD83E\uDD74 Película no encontrada");
        return "movie-list";
    }

    @PostMapping("")
    public String saveForm(@Valid @ModelAttribute Movie movie, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("movie", movie);
            model.addAttribute("returnUrl", "movie");
            return "movie-form";
        } else {
            movieRepository.save(movie);
            return "redirect:/movie-list" + movie.getId();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id) {
        if (id > 0 && movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
        }
        return "redirect:/movie-list";
    }
}