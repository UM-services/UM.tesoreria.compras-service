package tesoreria.compras.slice.articulo.infrastructure.web.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tesoreria.compras.slice.articulo.application.service.ArticuloService;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloNotFoundException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloSourceUnavailableException;
import tesoreria.compras.slice.articulo.infrastructure.web.controller.ArticuloController;
import tesoreria.compras.slice.articulo.infrastructure.web.exception.ArticuloExceptionHandler;
import tesoreria.compras.slice.articulo.infrastructure.web.mapper.ArticuloDtoMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ArticuloExceptionHandlerTest {

    @Mock
    private ArticuloService articuloService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ArticuloController(articuloService, new ArticuloDtoMapper()))
                .setControllerAdvice(new ArticuloExceptionHandler())
                .build();
    }

    @Test
    void mapsMissingArticuloToNotFound() throws Exception {
        when(articuloService.getArticuloById(101L)).thenThrow(new ArticuloNotFoundException(101L, new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/articulo/101"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe el artículo con ID 101"));
    }

    @Test
    void mapsUnavailableArticuloSourceToServiceUnavailable() throws Exception {
        when(articuloService.getArticuloById(101L))
                .thenThrow(new ArticuloSourceUnavailableException(new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/articulo/101"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("La fuente de artículos no está disponible"));
    }
}
