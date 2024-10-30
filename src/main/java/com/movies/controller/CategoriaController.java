package com.movies.controller;

import com.movies.model.Categoria;
import com.movies.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.repository.CrudRepository;

@Controller
@RequestMapping("/Categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository CategoriaRepository;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("Categorias", CategoriaRepository.findAll());
        return "Categoria-list";
    }

    @GetMapping("/new")
    public String createCategoria(Model model) {
        model.addAttribute("Categoria", new Categoria());
        return "Categoria-form";
    }

    @PostMapping
    public String saveCategoria(@ModelAttribute("Categoria") Categoria Categoria) {
        CategoriaRepository.save(Categoria);
        return "redirect:/Categorias";
    }

    @GetMapping("/edit/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        Categoria Categoria = CategoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Categoria ID:" + id));
        model.addAttribute("Categoria", Categoria);
        return "Categoria-form";
    }

    @PostMapping("/update/{id}")
    public String updateCategoria(@PathVariable("id") int id, @ModelAttribute("Categoria") Categoria Categoria) {
        Categoria.setId((long) id);
        CategoriaRepository.save(Categoria);
        return "redirect:/Categorias";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategoria(@PathVariable("id") int id) {
        CategoriaRepository.deleteById(id);
        return "redirect:/Categorias";
    }
}

