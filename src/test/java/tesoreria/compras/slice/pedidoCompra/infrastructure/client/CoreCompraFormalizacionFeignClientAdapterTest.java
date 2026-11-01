package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.domain.exception.FormalizacionConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.FormalizacionInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.FormalizacionNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionSolicitud;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreCompraFormalizacionFeignClientAdapterTest {

    @Mock private CoreCompraFormalizacionFeignClient client;

    @InjectMocks
    private CoreCompraFormalizacionFeignClientAdapter adapter;

    private Request request() {
        return Request.create(Request.HttpMethod.GET, "http://core-service.test/x", Map.of(), null,
                StandardCharsets.UTF_8);
    }

    private FeignException notFound() {
        return new FeignException.NotFound("nf", request(), new byte[0], Map.of());
    }

    private FeignException conflict() {
        return new FeignException.Conflict("c", request(), new byte[0], Map.of());
    }

    private FeignException bad() {
        return new FeignException.BadRequest("bad", request(), new byte[0], Map.of());
    }

    private FeignException down() {
        return new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of());
    }

    private CoreCompraFormalizacionResponse response() {
        return new CoreCompraFormalizacionResponse(1L, 9L, "OC-2026-000001", "OC-EXT", 15, null, 7, "ARS",
                new BigDecimal("28000.00"), "30 días", "48 horas", "12 meses", "<p>Objeto</p>", "obs", 38,
                List.of(new CoreCompraFormalizacionItemResponse(2L, 9L, 100, 1, new BigDecimal("3.00"), "Unidad",
                        "Notebook", null, new BigDecimal("9000.00"), new BigDecimal("27000.00"), "ACME")),
                null, null);
    }

    @Test
    void pendientesMapea() {
        when(client.pendientes(15)).thenReturn(List.of(new CoreCompraFormalizacionPendienteResponse(1L, "SC-1", 15,
                9L, 5L, 7, new BigDecimal("28000.00"), "ARS", "30 días", "48 horas", "12 meses",
                List.of(new CoreCompraFormalizacionItemResponse(null, 9L, 100, 1, new BigDecimal("3.00"), null,
                        "Notebook", null, null, null, null)))));

        var pendientes = adapter.pendientes(15);

        assertThat(pendientes).hasSize(1);
        assertThat(pendientes.get(0).compraCotizacionId()).isEqualTo(1L);
        assertThat(pendientes.get(0).items()).hasSize(1);
    }

    @Test
    void listarVacioUsaFindAll() {
        when(client.findAll()).thenReturn(List.of(response()));

        assertThat(adapter.listar(null)).hasSize(1);
        verify(client).findAll();
    }

    @Test
    void listarConFiltroUsaSearch() {
        when(client.search(any())).thenReturn(List.of(response()));

        assertThat(adapter.listar(new FormalizacionFiltro(15, null, null, null))).hasSize(1);
        verify(client).search(any());
    }

    @Test
    void listarConFiltroSinEjercicioUsaSearch() {
        when(client.search(any())).thenReturn(List.of());

        assertThat(adapter.listar(new FormalizacionFiltro(null, 7, null, null))).isEmpty();
        verify(client).search(any());
    }

    @Test
    void pendientesCaidaSeTraduce() {
        when(client.pendientes(15)).thenThrow(down());

        assertThatThrownBy(() -> adapter.pendientes(15)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void pendientesSinItemsDevuelveListaVacia() {
        when(client.pendientes(15)).thenReturn(List.of(new CoreCompraFormalizacionPendienteResponse(1L, "SC-1", 15,
                9L, 5L, 7, null, null, null, null, null, null)));

        assertThat(adapter.pendientes(15).get(0).items()).isEmpty();
    }

    @Test
    void getByIdSinItemsDevuelveListaVacia() {
        when(client.findById(1L)).thenReturn(new CoreCompraFormalizacionResponse(1L, 9L, "OC-2026-000001", null,
                15, null, 7, null, null, null, null, null, null, null, null, null, null, null));

        assertThat(adapter.getById(1L).items()).isEmpty();
    }

    @Test
    void listarCaidaSeTraduce() {
        when(client.findAll()).thenThrow(down());

        assertThatThrownBy(() -> adapter.listar(null)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void getByIdMapea() {
        when(client.findById(1L)).thenReturn(response());

        assertThat(adapter.getById(1L).numero()).isEqualTo("OC-2026-000001");
        assertThat(adapter.getById(1L).items()).hasSize(1);
    }

    @Test
    void getByIdNotFoundFalla() {
        when(client.findById(99L)).thenThrow(notFound());

        assertThatThrownBy(() -> adapter.getById(99L)).isInstanceOf(FormalizacionNotFoundException.class);
    }

    @Test
    void getByIdCaidaSeTraduce() {
        when(client.findById(1L)).thenThrow(down());

        assertThatThrownBy(() -> adapter.getById(1L)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void porCotizacionMapea() {
        when(client.porCotizacion(1L)).thenReturn(response());
        assertThat(adapter.porCotizacion(1L)).isPresent();
    }

    @Test
    void porCotizacionNotFoundDevuelveVacio() {
        when(client.porCotizacion(1L)).thenThrow(notFound());
        assertThat(adapter.porCotizacion(1L)).isEmpty();
    }

    @Test
    void porCotizacionCaidaSeTraduce() {
        when(client.porCotizacion(1L)).thenThrow(down());

        assertThatThrownBy(() -> adapter.porCotizacion(1L))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void formalizarCaidaSeTraduce() {
        doThrow(down()).when(client).formalizar(any());

        assertThatThrownBy(() -> adapter.formalizar(new FormalizacionSolicitud(1L, null, null, null, null, null,
                null, null, null), 38L)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void formalizarMapeaYEnviaElActor() {
        when(client.formalizar(any())).thenReturn(response());

        var oc = adapter.formalizar(new FormalizacionSolicitud(1L, "OC-EXT", "ARS", new BigDecimal("28000.00"),
                "30 días", "48 horas", "12 meses", "<p>Objeto</p>", "obs"), 38L);

        assertThat(oc.numero()).isEqualTo("OC-2026-000001");
        ArgumentCaptor<CoreCompraFormalizacionRequest> captor =
                ArgumentCaptor.forClass(CoreCompraFormalizacionRequest.class);
        verify(client).formalizar(captor.capture());
        assertThat(captor.getValue().compraCotizacionId()).isEqualTo(1L);
        assertThat(captor.getValue().usuarioId()).isEqualTo(38);
    }

    @Test
    void formalizarSinActorEnviaUsuarioNulo() {
        when(client.formalizar(any())).thenReturn(response());

        adapter.formalizar(new FormalizacionSolicitud(1L, null, null, null, null, null, null, null, null), null);

        ArgumentCaptor<CoreCompraFormalizacionRequest> captor =
                ArgumentCaptor.forClass(CoreCompraFormalizacionRequest.class);
        verify(client).formalizar(captor.capture());
        assertThat(captor.getValue().usuarioId()).isNull();
    }

    @Test
    void formalizarNotFoundSeTraduce() {
        doThrow(notFound()).when(client).formalizar(any());

        assertThatThrownBy(() -> adapter.formalizar(new FormalizacionSolicitud(1L, null, null, null, null, null,
                null, null, null), 38L)).isInstanceOf(FormalizacionNotFoundException.class);
    }

    @Test
    void formalizarConflictoSeTraduce() {
        doThrow(conflict()).when(client).formalizar(any());

        assertThatThrownBy(() -> adapter.formalizar(new FormalizacionSolicitud(1L, null, null, null, null, null,
                null, null, null), 38L)).isInstanceOf(FormalizacionConflictoException.class);
    }

    @Test
    void formalizarBadRequestSeTraduce() {
        doThrow(bad()).when(client).formalizar(any());

        assertThatThrownBy(() -> adapter.formalizar(new FormalizacionSolicitud(1L, null, null, null, null, null,
                null, null, null), 38L)).isInstanceOf(FormalizacionInvalidaException.class);
    }
}
