package tesoreria.compras.slice.proveedor.infrastructure.web.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorNotFoundException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorSourceUnavailableException;
import tesoreria.compras.slice.proveedor.application.service.ProveedorService;
import tesoreria.compras.slice.proveedor.infrastructure.web.controller.ProveedorPingController;
import tesoreria.compras.slice.proveedor.infrastructure.web.exception.ProveedorExceptionHandler;
import tesoreria.compras.slice.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProveedorExceptionHandlerTest {

    @Mock
    private ProveedorService proveedorService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProveedorPingController(proveedorService, new ProveedorDtoMapper()))
                .setControllerAdvice(new ProveedorExceptionHandler())
                .build();
    }

    @Test
    void mapsMissingProveedorToNotFound() throws Exception {
        when(proveedorService.getProveedorById(8)).thenThrow(new ProveedorNotFoundException(8, new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/ping/8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe el proveedor con ID 8"));
    }

    @Test
    void mapsUnavailableProveedorSourceToServiceUnavailable() throws Exception {
        when(proveedorService.getProveedorById(8)).thenThrow(new ProveedorSourceUnavailableException(new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/ping/8"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("La fuente de proveedores no está disponible"));
    }
}
