package tesoreria.compras.slice.pedidoCompra.infrastructure.web.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tesoreria.compras.slice.pedidoCompra.application.service.RecepcionService;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionNotFoundException;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller.RecepcionController;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.RecepcionDtoMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RecepcionExceptionHandlerTest {

    @Mock
    private RecepcionService recepcionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new RecepcionController(recepcionService, new RecepcionDtoMapper()))
                .setControllerAdvice(new PedidoCompraExceptionHandler())
                .build();
    }

    @Test
    void mapsNotFoundTo404() throws Exception {
        when(recepcionService.getById(1L)).thenThrow(new RecepcionNotFoundException("No existe la recepción con id: 1"));

        mockMvc.perform(get("/api/tesoreria/compras/recepcion/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe la recepción con id: 1"));
    }

    @Test
    void mapsInvalidaTo400() throws Exception {
        when(recepcionService.registrar(any(), any()))
                .thenThrow(new RecepcionInvalidaException("Los datos de la recepción son inválidos"));

        mockMvc.perform(post("/api/tesoreria/compras/recepcion")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"compraFormalizacionId\":50}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Los datos de la recepción son inválidos"));
    }

    @Test
    void mapsConflictoTo409() throws Exception {
        when(recepcionService.porFormalizacion(50L)).thenThrow(new RecepcionConflictoException("conflicto"));

        mockMvc.perform(get("/api/tesoreria/compras/recepcion/por-formalizacion/50"))
                .andExpect(status().isConflict());
    }
}
