package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionProcesoConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionProcesoInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionProcesoNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProceso;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreCompraCotizacionFeignClientAdapterTest {

    @Mock
    private CoreCompraCotizacionFeignClient client;

    @InjectMocks
    private CoreCompraCotizacionFeignClientAdapter adapter;

    private Request request() {
        return Request.create(Request.HttpMethod.GET, "http://core-service.test/x", Map.of(), null,
                StandardCharsets.UTF_8);
    }

    private FeignException bad() {
        return new FeignException.BadRequest("bad", request(), new byte[0], Map.of());
    }

    private FeignException notFound() {
        return new FeignException.NotFound("nf", request(), new byte[0], Map.of());
    }

    private FeignException conflict() {
        return new FeignException.Conflict("c", request(), new byte[0], Map.of());
    }

    private FeignException down() {
        return new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of());
    }

    private CoreCompraCotizacionResponse response() {
        return new CoreCompraCotizacionResponse(1L, null, 15, null, "BORRADOR", null, null, null, 2, null,
                null, null, null, null, 38,
                List.of(new CoreCompraCotizacionItemResponse(9L, 100, 1, new BigDecimal("3.00"))),
                List.of(new CoreCompraCotizacionProveedorResponse(5L, 7, "p@x.com", "PENDIENTE", null, null,
                        null, null, null, null, null, null, null, List.of())));
    }

    private CotizacionProceso proceso() {
        return new CotizacionProceso(null, null, 15, null, null, null, null, null, 2, null, null, null,
                null, null, 38,
                List.of(new CotizacionProcesoItem(null, 100, 1, new BigDecimal("3.00"))),
                List.of(new CotizacionProcesoProveedor(null, 7, "p@x.com", null, null, null, null, null,
                        null, null, null, null, null, List.of())));
    }

    @Test
    void pendientesMapea() {
        when(client.pendientes(eq(15), any())).thenReturn(List.of(
                new CoreCompraCotizacionPendienteResponse(100, 2, "PC-1", 14, 1, new BigDecimal("3.00"),
                        "Unidad", "Notebook", null, null)));

        assertThat(adapter.pendientes(15, List.of(14))).hasSize(1);
        assertThat(adapter.pendientes(15, null)).hasSize(1);
    }

    @Test
    void pendientesUnavailable() {
        when(client.pendientes(eq(15), any())).thenThrow(down());

        assertThatThrownBy(() -> adapter.pendientes(15, List.of(14)))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void respuestasPendientesMapea() {
        when(client.proveedoresPendientes(15)).thenReturn(List.of(
                new CoreCompraCotizacionProveedorPendienteResponse(1L, "SC-1", 15, null,
                        new CoreCompraCotizacionProveedorResponse(5L, 7, "p@x.com", "ENVIADA", null, null,
                                null, null, null, null, null, null, null, List.of()),
                        List.of(new CoreCompraCotizacionItemResponse(9L, 100, 1, new BigDecimal("3.00"))))));

        assertThat(adapter.respuestasPendientes(15)).hasSize(1);
        assertThat(adapter.respuestasPendientes(15).get(0).items()).hasSize(1);
    }

    @Test
    void respuestasPendientesUnavailable() {
        when(client.proveedoresPendientes(15)).thenThrow(down());

        assertThatThrownBy(() -> adapter.respuestasPendientes(15))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void adjudicarMapea() {
        when(client.adjudicar(eq(1L), any())).thenReturn(new CoreCompraAdjudicacionResponse(9L, 1L, 15, 5L, 7,
                new BigDecimal("28000.00"), "ADJUDICADA", "menor", 38L, new BigDecimal("300000.00"), null));

        assertThat(adapter.adjudicar(1L, "ADJUDICADA", 5L, 7, new BigDecimal("28000.00"), "menor", 38L,
                new BigDecimal("300000.00")).decision()).isEqualTo("ADJUDICADA");
    }

    @Test
    void adjudicarErrores() {
        when(client.adjudicar(eq(1L), any())).thenThrow(notFound());
        assertThatThrownBy(() -> adapter.adjudicar(1L, "ADJUDICADA", null, null, null, null, 38L, null))
                .isInstanceOf(CotizacionProcesoNotFoundException.class);

        when(client.adjudicar(eq(2L), any())).thenThrow(conflict());
        assertThatThrownBy(() -> adapter.adjudicar(2L, "ADJUDICADA", null, null, null, null, 38L, null))
                .isInstanceOf(CotizacionProcesoConflictoException.class);

        when(client.adjudicar(eq(3L), any())).thenThrow(down());
        assertThatThrownBy(() -> adapter.adjudicar(3L, "ADJUDICADA", null, null, null, null, 38L, null))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);

        when(client.adjudicar(eq(4L), any())).thenThrow(bad());
        assertThatThrownBy(() -> adapter.adjudicar(4L, "ADJUDICADA", null, null, null, null, 38L, null))
                .isInstanceOf(CotizacionProcesoInvalidaException.class);
    }

    @Test
    void adjudicacionMapeaODevuelveVacio() {
        when(client.adjudicacion(1L)).thenReturn(new CoreCompraAdjudicacionResponse(9L, 1L, 15, 5L, 7,
                new BigDecimal("28000.00"), "ADJUDICADA", "menor", 38L, new BigDecimal("300000.00"), null));
        assertThat(adapter.adjudicacion(1L)).isPresent();

        when(client.adjudicacion(2L)).thenThrow(notFound());
        assertThat(adapter.adjudicacion(2L)).isEmpty();
    }

    @Test
    void listarMapea() {
        when(client.findAll()).thenReturn(List.of(response()));

        assertThat(adapter.listar(null)).hasSize(1);
        assertThat(adapter.listar(null).get(0).items()).hasSize(1);
        assertThat(adapter.listar(null).get(0).proveedores()).hasSize(1);
    }

    @Test
    void listarConFiltroUsaSearch() {
        when(client.search(any())).thenReturn(List.of(response()));

        assertThat(adapter.listar(new CotizacionProcesoFiltro("BORRADOR", 15, null, null))).hasSize(1);
    }

    @Test
    void listarUnavailable() {
        when(client.findAll()).thenThrow(down());

        assertThatThrownBy(() -> adapter.listar(null)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void getByIdMapeaYErrores() {
        when(client.findById(1L)).thenReturn(response());
        assertThat(adapter.getById(1L).compraCotizacionId()).isEqualTo(1L);

        when(client.findById(99L)).thenThrow(notFound());
        assertThatThrownBy(() -> adapter.getById(99L)).isInstanceOf(CotizacionProcesoNotFoundException.class);
    }

    @Test
    void crearMapeaYErrores() {
        when(client.create(any())).thenReturn(response());
        assertThat(adapter.crear(proceso()).compraCotizacionId()).isEqualTo(1L);

        doThrow(bad()).when(client).create(any());
        assertThatThrownBy(() -> adapter.crear(proceso())).isInstanceOf(CotizacionProcesoInvalidaException.class);

        doThrow(conflict()).when(client).create(any());
        assertThatThrownBy(() -> adapter.crear(proceso())).isInstanceOf(CotizacionProcesoConflictoException.class);

        doThrow(down()).when(client).create(any());
        assertThatThrownBy(() -> adapter.crear(proceso())).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void actualizarMapeaYErrores() {
        when(client.update(eq(1L), any())).thenReturn(response());
        assertThat(adapter.actualizar(1L, proceso()).compraCotizacionId()).isEqualTo(1L);

        doThrow(notFound()).when(client).update(eq(1L), any());
        assertThatThrownBy(() -> adapter.actualizar(1L, proceso())).isInstanceOf(CotizacionProcesoNotFoundException.class);

        doThrow(bad()).when(client).update(eq(1L), any());
        assertThatThrownBy(() -> adapter.actualizar(1L, proceso())).isInstanceOf(CotizacionProcesoInvalidaException.class);

        doThrow(conflict()).when(client).update(eq(1L), any());
        assertThatThrownBy(() -> adapter.actualizar(1L, proceso())).isInstanceOf(CotizacionProcesoConflictoException.class);

        doThrow(down()).when(client).update(eq(1L), any());
        assertThatThrownBy(() -> adapter.actualizar(1L, proceso())).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void enviarMapeaYErrores() {
        when(client.enviar(1L)).thenReturn(response());
        assertThat(adapter.enviar(1L).compraCotizacionId()).isEqualTo(1L);

        doThrow(notFound()).when(client).enviar(1L);
        assertThatThrownBy(() -> adapter.enviar(1L)).isInstanceOf(CotizacionProcesoNotFoundException.class);

        doThrow(conflict()).when(client).enviar(1L);
        assertThatThrownBy(() -> adapter.enviar(1L)).isInstanceOf(CotizacionProcesoConflictoException.class);

        doThrow(down()).when(client).enviar(1L);
        assertThatThrownBy(() -> adapter.enviar(1L)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void anularMapeaYErrores() {
        when(client.anular(eq(1L), any())).thenReturn(response());
        assertThat(adapter.anular(1L, "x").compraCotizacionId()).isEqualTo(1L);

        doThrow(notFound()).when(client).anular(eq(1L), any());
        assertThatThrownBy(() -> adapter.anular(1L, "x")).isInstanceOf(CotizacionProcesoNotFoundException.class);

        doThrow(conflict()).when(client).anular(eq(1L), any());
        assertThatThrownBy(() -> adapter.anular(1L, "x")).isInstanceOf(CotizacionProcesoConflictoException.class);

        doThrow(down()).when(client).anular(eq(1L), any());
        assertThatThrownBy(() -> adapter.anular(1L, "x")).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    private CoreCompraCotizacionProveedorResponse proveedorResponse() {
        return new CoreCompraCotizacionProveedorResponse(5L, 7, "p@x.com", "RESPONDIDA", null, null, null,
                "ARS", new BigDecimal("100.00"), null, null, null, null,
                List.of(new CoreCompraCotizacionProveedorItemResponse(9L, 1L, true, new BigDecimal("10.00"),
                        new BigDecimal("50.00"), "Marca", null)));
    }

    private tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor proveedorConItems() {
        return new tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor(null, 7, "p@x.com",
                null, null, null, null, "ARS", new BigDecimal("100.00"), null, null, null, null,
                List.of(new tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedorItem(
                        null, 1L, true, new BigDecimal("10.00"), new BigDecimal("50.00"), "Marca", null)));
    }

    @Test
    void registrarRespuestaMapeaYErrores() {
        when(client.registrarRespuesta(eq(1L), eq(5L), any())).thenReturn(proveedorResponse());
        assertThat(adapter.registrarRespuesta(1L, 5L, proveedorConItems()).email()).isEqualTo("p@x.com");

        doThrow(notFound()).when(client).registrarRespuesta(eq(1L), eq(5L), any());
        assertThatThrownBy(() -> adapter.registrarRespuesta(1L, 5L, proveedorConItems()))
                .isInstanceOf(CotizacionProcesoNotFoundException.class);

        doThrow(conflict()).when(client).registrarRespuesta(eq(1L), eq(5L), any());
        assertThatThrownBy(() -> adapter.registrarRespuesta(1L, 5L, proveedorConItems()))
                .isInstanceOf(CotizacionProcesoConflictoException.class);

        doThrow(down()).when(client).registrarRespuesta(eq(1L), eq(5L), any());
        assertThatThrownBy(() -> adapter.registrarRespuesta(1L, 5L, proveedorConItems()))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void noCotizaMapeaYErrores() {
        when(client.marcarNoCotiza(eq(1L), eq(5L), any())).thenReturn(proveedorResponse());
        assertThat(adapter.marcarNoCotiza(1L, 5L, "sin stock")).isNotNull();

        doThrow(notFound()).when(client).marcarNoCotiza(eq(1L), eq(5L), any());
        assertThatThrownBy(() -> adapter.marcarNoCotiza(1L, 5L, "x"))
                .isInstanceOf(CotizacionProcesoNotFoundException.class);

        doThrow(conflict()).when(client).marcarNoCotiza(eq(1L), eq(5L), any());
        assertThatThrownBy(() -> adapter.marcarNoCotiza(1L, 5L, "x"))
                .isInstanceOf(CotizacionProcesoConflictoException.class);

        doThrow(down()).when(client).marcarNoCotiza(eq(1L), eq(5L), any());
        assertThatThrownBy(() -> adapter.marcarNoCotiza(1L, 5L, "x"))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void comparacionMapeaYErrores() {
        when(client.comparacion(1L)).thenReturn(response());
        assertThat(adapter.comparacion(1L).compraCotizacionId()).isEqualTo(1L);

        doThrow(notFound()).when(client).comparacion(1L);
        assertThatThrownBy(() -> adapter.comparacion(1L)).isInstanceOf(CotizacionProcesoNotFoundException.class);

        doThrow(down()).when(client).comparacion(1L);
        assertThatThrownBy(() -> adapter.comparacion(1L)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void recomendarMapeaYErrores() {
        when(client.recomendar(eq(1L), any())).thenReturn(response());
        assertThat(adapter.recomendar(1L, 5L, "mejor")).isNotNull();

        doThrow(notFound()).when(client).recomendar(eq(1L), any());
        assertThatThrownBy(() -> adapter.recomendar(1L, 5L, "x")).isInstanceOf(CotizacionProcesoNotFoundException.class);

        doThrow(conflict()).when(client).recomendar(eq(1L), any());
        assertThatThrownBy(() -> adapter.recomendar(1L, 5L, "x")).isInstanceOf(CotizacionProcesoConflictoException.class);

        doThrow(down()).when(client).recomendar(eq(1L), any());
        assertThatThrownBy(() -> adapter.recomendar(1L, 5L, "x"))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void cerrarMapeaYErrores() {
        when(client.cerrar(1L)).thenReturn(response());
        assertThat(adapter.cerrar(1L)).isNotNull();

        doThrow(notFound()).when(client).cerrar(1L);
        assertThatThrownBy(() -> adapter.cerrar(1L)).isInstanceOf(CotizacionProcesoNotFoundException.class);

        doThrow(conflict()).when(client).cerrar(1L);
        assertThatThrownBy(() -> adapter.cerrar(1L)).isInstanceOf(CotizacionProcesoConflictoException.class);

        doThrow(down()).when(client).cerrar(1L);
        assertThatThrownBy(() -> adapter.cerrar(1L)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }
}
