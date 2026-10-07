package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.PedidoCompraFixture;
import tesoreria.compras.slice.pedidoCompra.application.service.PedidoCompraService;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.PedidoCompraItemRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.PedidoCompraRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.PedidoCompraDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoCompraControllerTest {

    @Mock
    private PedidoCompraService pedidoCompraService;

    private PedidoCompraController controller;

    @BeforeEach
    void setUp() {
        controller = new PedidoCompraController(pedidoCompraService, new PedidoCompraDtoMapper());
    }

    @Test
    void iniciarDevuelveElContexto() {
        when(pedidoCompraService.getContexto(10L)).thenReturn(PedidoCompraFixture.contexto());

        var response = controller.iniciar(10L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().solicitante().nombre()).isEqualTo("Usuario Demo");
    }

    @Test
    void crearSinEnviar() {
        when(pedidoCompraService.crear(eq(10L), any(), eq(false))).thenReturn(PedidoCompraFixture.pedido());

        var response = controller.crear(10L, request(false));

        assertThat(response.getBody().compraPedidoId()).isEqualTo(1);
    }

    @Test
    void crearConEnviar() {
        when(pedidoCompraService.crear(eq(10L), any(), eq(true))).thenReturn(PedidoCompraFixture.pedido());

        var response = controller.crear(10L, request(true));

        assertThat(response.getBody().numero()).isEqualTo("PC-2026-000001");
    }

    @Test
    void actualizarDelega() {
        when(pedidoCompraService.actualizar(eq(10L), eq(1), any(), eq(false)))
                .thenReturn(PedidoCompraFixture.pedido());

        var response = controller.actualizar(10L, 1, request(false));

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(pedidoCompraService).actualizar(eq(10L), eq(1), any(), eq(false));
    }

    @Test
    void enviarDelega() {
        when(pedidoCompraService.enviar(10L, 1)).thenReturn(PedidoCompraFixture.pedido());

        assertThat(controller.enviar(10L, 1).getBody().numero()).isEqualTo("PC-2026-000001");
    }

    @Test
    void findByIdDelega() {
        when(pedidoCompraService.getById(1)).thenReturn(PedidoCompraFixture.pedido());

        assertThat(controller.findById(1).getBody().items()).hasSize(1);
    }

    @Test
    void listarDelega() {
        when(pedidoCompraService.listar(10L)).thenReturn(List.of(PedidoCompraFixture.pedido()));

        assertThat(controller.listar(10L).getBody()).hasSize(1);
    }

    private PedidoCompraRequest request(boolean enviar) {
        return new PedidoCompraRequest("Renovación", null, false, null, true,
                new BigDecimal("4500000.00"), null,
                List.of(new PedidoCompraItemRequest(1, new BigDecimal("5.00"), "Unidad", "Notebook", "16 GB", null)),
                enviar);
    }
}
