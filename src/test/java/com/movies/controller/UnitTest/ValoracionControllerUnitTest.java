package com.movies.controller.UnitTest;

import com.movies.controller.ValoracionController;
import com.movies.model.Customer;
import com.movies.model.Movie;
import com.movies.model.Valoracion;
import com.movies.repository.ValoracionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase ValoracionController.
 * Utiliza JUnit 5 y Mockito para verificar el comportamiento del controlador.
 */
@ExtendWith(MockitoExtension.class)
class ValoracionControllerUnitTest {

    // Controlador que se probará (System Under Test - SUT)
    @InjectMocks
    private ValoracionController valoracionController;

    // Repositorio simulado
    @Mock
    private ValoracionRepository valoracionRepository;

    // Modelo simulado para pasar datos a la vista
    @Mock
    private Model model;

    @Test
    @DisplayName("findById - La valoración existe")
    void findById_WhenValoracionExists() {
        // Configurar el mock para que devuelva una valoración simulada
        Customer mockCustomer = Customer.builder().id(1L).nombre("John").build();
        Movie mockMovie = Movie.builder().id(1L).name("Inception").build();
        Valoracion valoracion = Valoracion.builder()
                .id(1L)
                .customer(mockCustomer)
                .movie(mockMovie)
                .comentario("Comentario de prueba")
                .puntuacion(5)
                .build();
        when(valoracionRepository.findById(1L)).thenReturn(Optional.of(valoracion));

        // Ejecutar el método findById
        String view = valoracionController.findById(1L, model);

        // Verificar resultados
        assertEquals("valoracion-detail", view, "La vista debería ser 'valoracion-detail'");
        verify(valoracionRepository).findById(1L);
        verify(model).addAttribute("valoracion", valoracion);
    }

    @Test
    @DisplayName("findById - La valoración no existe")
    void findById_WhenValoracionNotExists() {
        // Configurar el mock para que no devuelva ninguna valoración
        when(valoracionRepository.findById(1L)).thenReturn(Optional.empty());

        // Ejecutar el método y verificar que lanza una excepción
        assertThrows(ResponseStatusException.class, () -> valoracionController.findById(1L, model));

        // Verificar que el repositorio fue consultado pero no el modelo
        verify(valoracionRepository).findById(1L);
        verifyNoInteractions(model);
    }

    @Test
    @DisplayName("save - Nueva valoración")
    void save_NewValoracion() {
        // Configurar una valoración simulada
        Valoracion nuevaValoracion = Valoracion.builder()
                .customer(Customer.builder().id(1L).build())
                .movie(Movie.builder().id(1L).build())
                .comentario("Nueva valoración")
                .puntuacion(4)
                .build();

        // Ejecutar el método save
        String view = valoracionController.save(nuevaValoracion);

        // Verificar resultados
        assertEquals("redirect:/valoraciones", view, "La vista debería redirigir a '/valoraciones'");
        verify(valoracionRepository).save(nuevaValoracion);
    }

    @Test
    @DisplayName("deleteById - Eliminación de valoración")
    void deleteById() {
        // Ejecutar el método deleteById
        String view = valoracionController.deleteValoracion(1L);

        // Verificar resultados
        assertEquals("redirect:/valoraciones", view, "La vista debería redirigir a '/valoraciones'");
        verify(valoracionRepository).deleteById(1L);
    }
}
