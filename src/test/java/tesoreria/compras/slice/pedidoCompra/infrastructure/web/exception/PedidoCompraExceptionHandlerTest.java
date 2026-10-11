package tesoreria.compras.slice.pedidoCompra.infrastructure.web.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tesoreria.compras.slice.pedidoCompra.application.service.PedidoCompraService;
import tesoreria.compras.slice.pedidoCompra.domain.exception.*;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller.PedidoCompraController;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.PedidoCompraDtoMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PedidoCompraExceptionHandlerTest {

    @Mock
    private PedidoCompraService pedidoCompraService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PedidoCompraController(pedidoCompraService, new PedidoCompraDtoMapper()))
                .setControllerAdvice(new PedidoCompraExceptionHandler())
                .build();
    }

    @Test
    void mapsNotFoundTo404() throws Exception {
        when(pedidoCompraService.getById(10L, 1))
                .thenThrow(new PedidoCompraNotFoundException(1, new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/pedido/1").header("X-User-Id", "10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No se encontró el pedido de compra 1"));
    }

    @Test
    void mapsConflictTo409() throws Exception {
        when(pedidoCompraService.enviar(10L, 1))
                .thenThrow(new PedidoCompraEstadoInvalidoException(1, new RuntimeException()));

        mockMvc.perform(post("/api/tesoreria/compras/pedido/1/enviar").header("X-User-Id", "10"))
                .andExpect(status().isConflict());
    }

    @Test
    void mapsPermisoDenegadoTo403() throws Exception {
        when(pedidoCompraService.getContexto(10L))
                .thenThrow(new PermisoDenegadoException("compras.iniciar_pedido"));

        mockMvc.perform(get("/api/tesoreria/compras/pedido/iniciar").header("X-User-Id", "10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("Permiso requerido: compras.iniciar_pedido"));
    }

    @Test
    void mapsIdentidadRequeridaTo401() throws Exception {
        when(pedidoCompraService.listar(null)).thenThrow(new IdentidadRequeridaException());

        mockMvc.perform(get("/api/tesoreria/compras/pedido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mapsSourceUnavailableTo503() throws Exception {
        when(pedidoCompraService.getById(10L, 1))
                .thenThrow(new PedidoCompraSourceUnavailableException(new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/pedido/1").header("X-User-Id", "10"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void mapsAccesoPedidoDenegadoTo403() throws Exception {
        when(pedidoCompraService.getById(10L, 1)).thenThrow(new AccesoPedidoDenegadoException(1));

        mockMvc.perform(get("/api/tesoreria/compras/pedido/1").header("X-User-Id", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void mapsDependenciaNoAsignadaTo409() throws Exception {
        when(pedidoCompraService.crear(eq(74L), any(), eq(false)))
                .thenThrow(new DependenciaNoAsignadaException());

        mockMvc.perform(post("/api/tesoreria/compras/pedido")
                        .header("X-User-Id", "74")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"necesidad\":\"prueba\",\"enviar\":false}"))
                .andExpect(status().isConflict());
    }

    @Test
    void mapsDependenciaNoAutorizadaTo403() throws Exception {
        when(pedidoCompraService.aprobar(10L, 1))
                .thenThrow(new DependenciaNoAutorizadaException(20));

        mockMvc.perform(post("/api/tesoreria/compras/pedido/1/aprobar").header("X-User-Id", "10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value(
                        "No está autorizado a decidir sobre pedidos de la dependencia 20"));
    }

    @Test
    void mapsLimiteExcedidoTo403ConCodigo() throws Exception {
        when(pedidoCompraService.autorizarPresupuesto(10L, 1))
                .thenThrow(new LimiteAutorizacionExcedidoException(1,
                        new BigDecimal("5000000.00"), new BigDecimal("4500000.00")));

        mockMvc.perform(post("/api/tesoreria/compras/pedido/1/autorizar-presupuesto")
                        .header("X-User-Id", "10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("LIMITE_AUTORIZACION_EXCEDIDO"))
                .andExpect(jsonPath("$.monto").value(5000000.00));
    }
}
