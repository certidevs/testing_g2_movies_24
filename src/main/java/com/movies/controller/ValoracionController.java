package com.movies.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.movies.model.Valoracion;
import com.movies.repository.ValoracionRepository;
import org.springframework.web.server.ResponseStatusException;

@Controller
@AllArgsConstructor
//@RequestMapping("/valoraciones")

public class ValoracionController {


    private ValoracionRepository valoracionRepository;

    @GetMapping("valoraciones")
    public String findAll(Model model) {
        model.addAttribute("valoraciones", valoracionRepository.findAll());
        return "valoracion-list";
    }
    @GetMapping("valoraciones/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid valoracion ID:" + id));
        model.addAttribute("valoracion", valoracion);
        return "valoracion-detail";
    }

    @GetMapping("valoraciones/new")
    public String createValoracion(Model model) {
        model.addAttribute("valoracion", new Valoracion());
        return "valoracion-form";
    }

    @GetMapping("valoraciones/edit/{id}")
    public String updateValoracion(Model model, @PathVariable Integer id) {
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("valoracion", valoracion);
        return "valoracion-form";
    }

    @PostMapping("valoraciones")
    public String save(@ModelAttribute Valoracion valoracion) {
        valoracionRepository.save(valoracion);
        return "redirect:/valoraciones";
    }



    @GetMapping("valoraciones/delete/{id}")
    public String deleteCustomer(@PathVariable("id") int id) {
        valoracionRepository.deleteById(id);
        return "redirect:/valoraciones";
    }
}
