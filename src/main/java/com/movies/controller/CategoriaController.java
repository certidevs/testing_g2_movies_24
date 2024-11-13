package com.movies.controller;

import com.movies.model.Categoria;
import com.movies.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.repository.CrudRepository;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository CategoriaRepository;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("categorias", CategoriaRepository.findAll());
        return "categoria-list";
    }

    @GetMapping("/new")
    public String createCategoria(Model model) {
        model.addAttribute("categorias", new Categoria());
        model.addAttribute("returnUrl", "categorias");
        return "categoria-form";
    }

    @PostMapping
    public String saveCategoria(@ModelAttribute("categorias") Categoria Categoria) {
        CategoriaRepository.save(Categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/edit/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        Categoria Categoria = CategoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Categoria no valido:" + id));
        model.addAttribute("categorias", Categoria);
        return "categoria-form";
    }

    @PostMapping("/update/{id}")
    public String updateCategoria(@PathVariable("id") int id, @ModelAttribute("Categorias") Categoria Categoria) {
        Categoria.setId((long) id);
        CategoriaRepository.save(Categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategoria(@PathVariable("id") int id) {
        CategoriaRepository.deleteById(id);
        return "redirect:/categorias";
    }
}

