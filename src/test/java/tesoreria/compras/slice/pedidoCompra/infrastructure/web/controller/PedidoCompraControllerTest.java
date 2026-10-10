package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.PedidoCompraFixture;
import tesoreria.compras.slice.pedidoCompra.application.service.PedidoCompraService;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.BandejaPedidoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.ConsultaPedidoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.DescartarPedidoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.EstimarPedidoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.PedidoCompraItemRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.PedidoCompraRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RechazarPedidoRequest;
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
    void descartarDelega() {
        when(pedidoCompraService.descartar(10L, 1, "No hace falta")).thenReturn(PedidoCompraFixture.pedido());

        controller.descartar(10L, 1, new DescartarPedidoRequest("No hace falta"));

        verify(pedidoCompraService).descartar(10L, 1, "No hace falta");
    }

    @Test
    void aprobarDelega() {
        when(pedidoCompraService.aprobar(10L, 1)).thenReturn(PedidoCompraFixture.pedido());

        assertThat(controller.aprobar(10L, 1).getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void rechazarDelega() {
        when(pedidoCompraService.rechazar(10L, 1, "Falta cotización"))
                .thenReturn(PedidoCompraFixture.pedidoRechazado());

        controller.rechazar(10L, 1, new RechazarPedidoRequest("Falta cotización"));

        verify(pedidoCompraService).rechazar(10L, 1, "Falta cotización");
    }

    @Test
    void bandejaDelega() {
        when(pedidoCompraService.bandeja(10L, "PENDIENTE_ENVIO"))
                .thenReturn(List.of(PedidoCompraFixture.resumen()));

        assertThat(controller.bandeja(10L, new BandejaPedidoRequest("PENDIENTE_ENVIO")).getBody()).hasSize(1);
    }

    @Test
    void consultaDelega() {
        when(pedidoCompraService.consulta(eq(10L), any(PedidoCompraFiltro.class)))
                .thenReturn(List.of(PedidoCompraFixture.resumen()));

        assertThat(controller.consulta(10L, new ConsultaPedidoRequest("ENVIADO", null, null, null, null)).getBody()).hasSize(1);
    }

    @Test
    void findByIdDelega() {
        when(pedidoCompraService.getById(10L, 1)).thenReturn(PedidoCompraFixture.pedido());

        assertThat(controller.findById(10L, 1).getBody().items()).hasSize(1);
    }

    @Test
    void listarDelega() {
        when(pedidoCompraService.listar(10L)).thenReturn(List.of(PedidoCompraFixture.pedido()));

        assertThat(controller.listar(10L).getBody()).hasSize(1);
    }

    @Test
    void historialDelega() {
        when(pedidoCompraService.historial(10L, 1)).thenReturn(List.of(PedidoCompraFixture.historial()));

        assertThat(controller.historial(10L, 1).getBody()).hasSize(1);
    }

    @Test
    void revisionDelega() {
        when(pedidoCompraService.revision(10L, null)).thenReturn(List.of(PedidoCompraFixture.resumen()));

        assertThat(controller.revision(10L, null).getBody()).hasSize(1);
    }

    @Test
    void presupuestoBandejaDelega() {
        when(pedidoCompraService.presupuestoBandeja(10L)).thenReturn(List.of(PedidoCompraFixture.resumen()));

        assertThat(controller.presupuestoBandeja(10L).getBody()).hasSize(1);
    }

    @Test
    void limiteDelega() {
        when(pedidoCompraService.limite(10L, 7))
                .thenReturn(PedidoCompraFixture.limite(new BigDecimal("4500000.00"), false, true));

        assertThat(controller.limite(10L, 7).getBody().limite()).isEqualByComparingTo("4500000.00");
    }

    @Test
    void estimarDelega() {
        when(pedidoCompraService.estimar(10L, 1, new BigDecimal("4500000.00"), "fuente"))
                .thenReturn(PedidoCompraFixture.pedido());

        var response = controller.estimar(10L, 1, new EstimarPedidoRequest(new BigDecimal("4500000.00"), "fuente"));

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(pedidoCompraService).estimar(10L, 1, new BigDecimal("4500000.00"), "fuente");
    }

    @Test
    void autorizarPresupuestoDelega() {
        when(pedidoCompraService.autorizarPresupuesto(10L, 1))
                .thenReturn(PedidoCompraFixture.pedidoPendientePresupuesto());

        assertThat(controller.autorizarPresupuesto(10L, 1).getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void rechazarPresupuestoDelega() {
        when(pedidoCompraService.rechazarPresupuesto(10L, 1, "fuera de política"))
                .thenReturn(PedidoCompraFixture.pedidoRechazado());

        controller.rechazarPresupuesto(10L, 1, new RechazarPedidoRequest("fuera de política"));

        verify(pedidoCompraService).rechazarPresupuesto(10L, 1, "fuera de política");
    }

    private PedidoCompraRequest request(boolean enviar) {
        return new PedidoCompraRequest("Renovación", null, false, null, true,
                new BigDecimal("4500000.00"), null,
                List.of(new PedidoCompraItemRequest(1, new BigDecimal("5.00"), "Unidad", "Notebook", "16 GB", null)),
                enviar);
    }
}
