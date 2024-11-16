package com.movies.controller;

import com.movies.model.Categoria;
import com.movies.repository.CategoriaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.repository.CrudRepository;
@AllArgsConstructor
@Controller
//@RequestMapping("/categorias")

public class CategoriaController {

    //@Autowired
    private CategoriaRepository CategoriaRepository;

    @GetMapping("categorias")
    public String findAll(Model model) {
        model.addAttribute("categorias", CategoriaRepository.findAll());
        return "categoria-list";
    }

    @GetMapping("categorias/new")
    public String createCategoria(Model model) {
        model.addAttribute("categorias", new Categoria());
        model.addAttribute("returnUrl", "categorias");
        return "categoria-form";
    }

    @PostMapping("categorias")
    public String saveCategoria(@ModelAttribute("categorias") Categoria Categoria) {
        CategoriaRepository.save(Categoria);
        return "redirect:/categorias";
    }

    @GetMapping("categorias/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        Categoria Categoria = CategoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Categoria no valido:" + id));
        model.addAttribute("categorias", Categoria);
        return "categoria-form";
    }

    @PostMapping("categorias/update/{id}")
    public String updateCategoria(@PathVariable("id") int id, @ModelAttribute("Categorias") Categoria Categoria) {
        Categoria.setId((long) id);
        CategoriaRepository.save(Categoria);
        return "redirect:/categorias";
    }

    @GetMapping("categorias/delete/{id}")
    public String deleteCategoria(@PathVariable("id") int id) {
        CategoriaRepository.deleteById(id);
        return "redirect:/categorias";
    }
}

