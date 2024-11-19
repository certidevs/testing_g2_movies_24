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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test unitario para ValoracionController utilizando JUnit 5 y Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ValoracionControllerUnitTest {

    @InjectMocks
    private ValoracionController valoracionController;

    @Mock
    private ValoracionRepository valoracionRepository;

    @Mock
    private Model model;

    @Test
    @DisplayName("findById que SÍ tiene valoración con id")
    void findById_WhenValoracionExists() {
        // 1. Configurar respuestas mocks
        Customer mockUsuario = mock(Customer.class); // Simulación de Customer
        Movie mockPelicula = mock(Movie.class);      // Simulación de Movie
        Valoracion valoracion = Valoracion.builder()
                .id(1L)
                .customer(mockUsuario)
                .movie(mockPelicula)
                .comentario("Comentario de prueba")
                .puntuacion(5)
                .build();
        Optional<Valoracion> valoracionOpt = Optional.of(valoracion);

        when(valoracionRepository.findById(1L)).thenReturn(valoracionOpt);

        // 2. Ejecutar método a testear
        String view = valoracionController.findById(1L, model);

        // 3. Aserciones y verificaciones
        assertEquals("valoracion-detail", view);
        verify(valoracionRepository).findById(1L);
        verify(model).addAttribute("valoracion", valoracion);
    }

    @Test
    @DisplayName("findById que NO tiene valoración")
    void findById_WhenValoracionNotExists() {
        // 1. Configurar respuestas mocks
        when(valoracionRepository.findById(1L)).thenReturn(Optional.empty());

        // 2. Ejecutar método a testear
        String view = valoracionController.findById(1L, model);

        // 3. Aserciones y verificaciones
        assertEquals("valoracion-detail", view);
        verify(valoracionRepository).findById(1L);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("save cuando se crea una nueva valoración")
    void save_NewValoracion() {
        // 1. Preparar datos y mocks
        Valoracion nuevaValoracion = Valoracion.builder()
                .customer(mock(Customer.class))
                .movie(mock(Movie.class))
                .comentario("Nueva valoración")
                .puntuacion(4)
                .build();

        // 2. Ejecutar método a testear
        String view = valoracionController.save(nuevaValoracion);

        // 3. Verificaciones
        assertEquals("redirect:/valoraciones", view);
        verify(valoracionRepository).save(nuevaValoracion);
    }

    @Test
    @DisplayName("deleteById elimina valoración por id")
    void deleteById() {
        // 1. Ejecutar método a testear
        String view = valoracionController.deleteValoracion(1L);

        // 2. Verificaciones
        assertEquals("redirect:/valoraciones", view);
        verify(valoracionRepository).deleteById(1L);
    }
}
