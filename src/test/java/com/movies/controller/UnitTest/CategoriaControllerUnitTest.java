package com.movies.controller.UnitTest;

import com.movies.controller.CategoriaController;
import com.movies.model.Categoria;
import com.movies.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class CategoriaControllerUnitTest {
    @InjectMocks
    private CategoriaController categoriaController;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    Model model;

    @Test
    void findAll(){
        when (categoriaRepository.findAll()).thenReturn(List.of(
                Categoria.builder().id(1L).build()));
        String view = categoriaController.findAll(model);
        verify(categoriaRepository).findAll();
        assertEquals("categoria-list", view);
    }

    @Test
    void findById(){
        Categoria categoria = Categoria.builder().id(1L).build();

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        String view = categoriaController.findById(1L, model);
        assertEquals("categoria-detail", view);
        verify(categoriaRepository).findById(1L);
        verify(model).addAttribute("categoria", categoria);
    }
    @Test
    void findById_CategoriaNotFound(){
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoriaController.findById(1L, model);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("categoria not found", exception.getReason());
        verify(categoriaRepository).findById(1L);
        verify(model, never()).addAttribute(eq("categoria"), any());
    }
    @Test
    void findById_IdNotFound(){
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoriaController.findById_NotExist(1L, model);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("categoria not found", exception.getReason());
        verify(categoriaRepository).findById(1L);
        verify(model, never()).addAttribute(eq("categoria"), any());
    }

    @Test
    void getFormCreateCategoria(){
        Categoria categoria = new Categoria();
        String view = categoriaController.getFormCreateCategoria(model);
        assertEquals("categoria-form", view);
        verify(model).addAttribute(eq("categoria"), any(Categoria.class));
    }

    @Test
    void getFormUpdateCategoria(){
        Categoria categoria = Categoria.builder().id(1L).build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        String view = categoriaController.getFormUpdateCategoria(model, 1L);
        assertEquals("categoria-form", view);
        verify(model).addAttribute("categoria", categoria);
        verify(categoriaRepository).findById(1L);
    }
    @Test
    void getFormUpdateCategoria_NotFound(){
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            categoriaController.getFormUpdateCategoria(model, 1L);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("not found", exception.getReason());
        verify(categoriaRepository).findById(1L);
        verify(model, never()).addAttribute(eq("categoria"), any());
    }

    @Test
    void saveCategoriaNew(){
        Categoria categoria = new Categoria();
        String view = categoriaController.saveCategoria(categoria);
        assertEquals("redirect:/categorias", view);
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void saveCategoriaUpdate(){
        Categoria categoria = Categoria.builder().id(1L).build();
        Categoria categoriaUpdate = Categoria.builder().id(1L).nombre("Editado").build();
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        String view = categoriaController.saveCategoria(categoriaUpdate);
        assertEquals("redirect:/categorias", view);
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository).save(categoria);
        assertEquals(categoriaUpdate.getNombre(), categoria.getNombre());
    }

    @Test
    void deleteCategoria(){
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        String view = categoriaController.deleteCategoria(1L);
        assertEquals("redirect:/categorias", view);
        verify(categoriaRepository).deleteById(1L);
    }
}
