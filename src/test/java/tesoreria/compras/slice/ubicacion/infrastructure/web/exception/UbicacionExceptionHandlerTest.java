package tesoreria.compras.slice.ubicacion.infrastructure.web.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tesoreria.compras.slice.ubicacion.application.service.UbicacionService;
import tesoreria.compras.slice.ubicacion.domain.exception.UbicacionSourceUnavailableException;
import tesoreria.compras.slice.ubicacion.infrastructure.web.controller.UbicacionController;
import tesoreria.compras.slice.ubicacion.infrastructure.web.mapper.UbicacionDtoMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UbicacionExceptionHandlerTest {

    @Mock
    private UbicacionService ubicacionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UbicacionController(ubicacionService, new UbicacionDtoMapper()))
                .setControllerAdvice(new UbicacionExceptionHandler())
                .build();
    }

    @Test
    void mapsUnavailableSource() throws Exception {
        when(ubicacionService.getUbicaciones())
                .thenThrow(new UbicacionSourceUnavailableException(new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/ubicacion/"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("La fuente de ubicaciones no está disponible"));
    }
}
