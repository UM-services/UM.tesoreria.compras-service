package tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tesoreria.compras.slice.ubicacionArticulo.application.service.UbicacionArticuloService;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloConflictException;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloSourceUnavailableException;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloValidationException;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.controller.UbicacionArticuloController;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.mapper.UbicacionArticuloDtoMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UbicacionArticuloExceptionHandlerTest {

    @Mock
    private UbicacionArticuloService ubicacionArticuloService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UbicacionArticuloController(ubicacionArticuloService, new UbicacionArticuloDtoMapper()))
                .setControllerAdvice(new UbicacionArticuloExceptionHandler())
                .build();
    }

    @Test
    void mapsValidationError() throws Exception {
        when(ubicacionArticuloService.getByArticulo(101L))
                .thenThrow(new UbicacionArticuloValidationException(new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/ubicacionArticulo/articulo/101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("La asignación de imputación no es válida."));
    }

    @Test
    void mapsConflict() throws Exception {
        when(ubicacionArticuloService.getByArticulo(101L))
                .thenThrow(new UbicacionArticuloConflictException(new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/ubicacionArticulo/articulo/101"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("La asignación choca con otro dato."));
    }

    @Test
    void mapsUnavailableSource() throws Exception {
        when(ubicacionArticuloService.getByArticulo(101L))
                .thenThrow(new UbicacionArticuloSourceUnavailableException(new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/ubicacionArticulo/articulo/101"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("La fuente de imputaciones no está disponible"));
    }
}
