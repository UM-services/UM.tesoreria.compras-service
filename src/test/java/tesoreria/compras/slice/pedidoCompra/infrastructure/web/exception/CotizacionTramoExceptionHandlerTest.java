package tesoreria.compras.slice.pedidoCompra.infrastructure.web.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tesoreria.compras.slice.pedidoCompra.application.service.CotizacionTramoService;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionTramoConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionTramoInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionTramoNotFoundException;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller.CotizacionTramoController;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.CotizacionTramoDtoMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CotizacionTramoExceptionHandlerTest {

    @Mock
    private CotizacionTramoService cotizacionTramoService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CotizacionTramoController(cotizacionTramoService, new CotizacionTramoDtoMapper()))
                .setControllerAdvice(new PedidoCompraExceptionHandler())
                .build();
    }

    @Test
    void mapsInvalidaTo400() throws Exception {
        when(cotizacionTramoService.crear(any())).thenThrow(new CotizacionTramoInvalidaException());

        mockMvc.perform(post("/api/tesoreria/compras/cotizacion/tramos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"N1\",\"topeMultiplico\":2,\"cantidadMinima\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mapsConflictoTo409() throws Exception {
        when(cotizacionTramoService.actualizar(eq(1L), any()))
                .thenThrow(new CotizacionTramoConflictoException("Ya existe un tramo con tope 2"));

        mockMvc.perform(put("/api/tesoreria/compras/cotizacion/tramos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"N1\",\"topeMultiplico\":2,\"cantidadMinima\":1}"))
                .andExpect(status().isConflict());
    }

    @Test
    void mapsNotFoundTo404() throws Exception {
        doThrow(new CotizacionTramoNotFoundException(1L)).when(cotizacionTramoService).eliminar(1L);

        mockMvc.perform(delete("/api/tesoreria/compras/cotizacion/tramos/1"))
                .andExpect(status().isNotFound());
    }
}
