package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.application.service.CotizacionProcesoService;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProceso;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedorPendiente;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoCriteriaRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoItemRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoProveedorRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoRespuestaRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecomendarCotizacionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.CotizacionProcesoDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CotizacionProcesoControllerTest {

    @Mock
    private CotizacionProcesoService service;

    private CotizacionProcesoController controller;

    @BeforeEach
    void setUp() {
        controller = new CotizacionProcesoController(service, new CotizacionProcesoDtoMapper());
    }

    private CotizacionProceso proceso() {
        return new CotizacionProceso(1L, null, 15, null, "BORRADOR", null, null, null, 2, null,
                null, null, null, null, 38, List.of(), List.of());
    }

    private CotizacionProcesoRequest request() {
        return new CotizacionProcesoRequest(15, null, "obs", null, new BigDecimal("100.00"), 2, null, 38,
                List.of(new CotizacionProcesoItemRequest(100, 1, new BigDecimal("3.00"))),
                List.of(new CotizacionProcesoProveedorRequest(7, "p@x.com", null)));
    }

    @Test
    void pendientesDevuelveLaLista() {
        when(service.pendientes(eq(15), any())).thenReturn(List.of(
                new CotizacionPendiente(100, 2, "PC-1", 14, 1, null, "Unidad", "Notebook", null, null)));

        var response = controller.pendientes(15, List.of(14));

        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).compraPedidoItemId()).isEqualTo(100);
    }

    @Test
    void listarDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(proceso()));

        assertThat(controller.listar().getBody()).hasSize(1);
    }

    @Test
    void proveedoresPendientesDevuelveLaLista() {
        when(service.respuestasPendientes(15)).thenReturn(List.of(
                new CotizacionProcesoProveedorPendiente(1L, "SC-1", 15, null,
                        new CotizacionProcesoProveedor(5L, 7, "p@x.com", "ENVIADA", null, null, null, null,
                                null, null, null, null, null, List.of()),
                        List.of(new CotizacionProcesoItem(9L, 100, 1, new BigDecimal("3.00"))))));

        var response = controller.proveedoresPendientes(15);

        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).numero()).isEqualTo("SC-1");
        assertThat(response.getBody().get(0).items()).hasSize(1);
    }

    @Test
    void searchDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(proceso()));

        assertThat(controller.search(new CotizacionProcesoCriteriaRequest("BORRADOR", 15, null, null)).getBody())
                .hasSize(1);
    }

    @Test
    void findByIdDevuelveElProceso() {
        when(service.getById(1L)).thenReturn(proceso());

        assertThat(controller.findById(1L).getBody().compraCotizacionId()).isEqualTo(1L);
    }

    @Test
    void crearDevuelveElProceso() {
        when(service.crear(any())).thenReturn(proceso());

        assertThat(controller.crear(request()).getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void actualizarDelega() {
        when(service.actualizar(eq(1L), any())).thenReturn(proceso());

        assertThat(controller.actualizar(1L, request()).getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void enviarDelega() {
        when(service.enviar(1L)).thenReturn(proceso());

        assertThat(controller.enviar(1L).getStatusCode().value()).isEqualTo(200);
        verify(service).enviar(1L);
    }

    @Test
    void anularDelega() {
        when(service.anular(eq(1L), any())).thenReturn(proceso());

        assertThat(controller.anular(1L, new CotizacionProcesoController.MotivoRequest("x")).getStatusCode().value())
                .isEqualTo(200);
        assertThat(controller.anular(1L, null).getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void etapa6CDelega() {
        when(service.registrarRespuesta(eq(1L), eq(5L), any())).thenReturn(null);
        assertThat(controller.registrarRespuesta(1L, 5L, new CotizacionProcesoRespuestaRequest(null, "ARS",
                null, null, null, null, null, List.of())).getStatusCode().value()).isEqualTo(200);

        when(service.marcarNoCotiza(eq(1L), eq(5L), any())).thenReturn(null);
        assertThat(controller.marcarNoCotiza(1L, 5L, new CotizacionProcesoController.MotivoRequest("x"))
                .getStatusCode().value()).isEqualTo(200);

        when(service.comparacion(1L)).thenReturn(proceso());
        assertThat(controller.comparacion(1L).getStatusCode().value()).isEqualTo(200);

        when(service.recomendar(eq(1L), eq(5L), any())).thenReturn(proceso());
        assertThat(controller.recomendar(1L, new RecomendarCotizacionRequest(5L, "mejor"))
                .getStatusCode().value()).isEqualTo(200);

        when(service.cerrar(1L)).thenReturn(proceso());
        assertThat(controller.cerrar(1L).getStatusCode().value()).isEqualTo(200);
    }
}
