package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionItemSolicitud;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionSolicitud;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreCompraRecepcionFeignClientAdapterTest {

    @Mock private CoreCompraRecepcionFeignClient client;

    @InjectMocks
    private CoreCompraRecepcionFeignClientAdapter adapter;

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

    private CoreCompraRecepcionResponse response() {
        return new CoreCompraRecepcionResponse(1L, 50L, "RC-2026-000001", 15, null, "R-1", "CONFORME",
                "NO_CORRESPONDE", "obs", 38,
                List.of(new CoreCompraRecepcionItemResponse(2L, 100L, 1, new BigDecimal("2.00"), "CONFORME", null)),
                null, null);
    }

    private CoreCompraRecepcionPendienteResponse pendienteResponse() {
        return new CoreCompraRecepcionPendienteResponse(50L, "OC-2026-000003", "OC-EXT", 15, 7, null,
                new BigDecimal("28000.00"), "ARS",
                List.of(new CoreCompraRecepcionPendienteItemResponse(100L, 1000, 1, new BigDecimal("3.00"),
                        new BigDecimal("1.00"), new BigDecimal("2.00"), "Unidad", "Notebook", null,
                        new BigDecimal("9000.00"), new BigDecimal("27000.00"), "ACME", false)),
                List.of(response()), false);
    }

    private RecepcionSolicitud solicitud() {
        return new RecepcionSolicitud(50L, LocalDateTime.of(2026, 10, 14, 9, 0), "R-1", "INTERVENIDO", "obs",
                List.of(new RecepcionItemSolicitud(100L, 1, new BigDecimal("2.00"), "CONFORME", null)));
    }

    @Test
    void pendientesMapea() {
        when(client.pendientes(15)).thenReturn(List.of(pendienteResponse()));

        var pendientes = adapter.pendientes(15);

        assertThat(pendientes).hasSize(1);
        assertThat(pendientes.get(0).items()).hasSize(1);
        assertThat(pendientes.get(0).items().get(0).saldo()).isEqualByComparingTo("2.00");
        assertThat(pendientes.get(0).recepciones()).hasSize(1);
    }

    @Test
    void pendientesSinDetalleDevuelveListasVacias() {
        when(client.pendientes(15)).thenReturn(List.of(new CoreCompraRecepcionPendienteResponse(50L, "OC-1", null,
                15, 7, null, null, null, null, null, null)));

        var pendiente = adapter.pendientes(15).get(0);

        assertThat(pendiente.items()).isEmpty();
        assertThat(pendiente.recepciones()).isEmpty();
    }

    @Test
    void pendientesCaidaSeTraduce() {
        when(client.pendientes(15)).thenThrow(down());

        assertThatThrownBy(() -> adapter.pendientes(15)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void listarVacioUsaFindAll() {
        when(client.findAll()).thenReturn(List.of(response()));

        assertThat(adapter.listar(null)).hasSize(1);
        verify(client).findAll();
    }

    @Test
    void listarConFiltroVacioUsaFindAll() {
        when(client.findAll()).thenReturn(List.of());

        assertThat(adapter.listar(new RecepcionFiltro(null, null, "  ", null, null))).isEmpty();
        verify(client).findAll();
    }

    @Test
    void listarConFiltroUsaSearch() {
        when(client.search(any())).thenReturn(List.of(response()));

        assertThat(adapter.listar(new RecepcionFiltro(15, 50L, "CONFORME", null, null))).hasSize(1);
        verify(client).search(any());
    }

    @Test
    void listarCaidaSeTraduce() {
        when(client.findAll()).thenThrow(down());

        assertThatThrownBy(() -> adapter.listar(null)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void getByIdMapea() {
        when(client.findById(1L)).thenReturn(response());

        assertThat(adapter.getById(1L).numero()).isEqualTo("RC-2026-000001");
        assertThat(adapter.getById(1L).items()).hasSize(1);
    }

    @Test
    void getByIdSinDetalleDevuelveListaVacia() {
        when(client.findById(1L)).thenReturn(new CoreCompraRecepcionResponse(1L, 50L, "RC-1", 15, null, null,
                null, null, null, null, null, null, null));

        assertThat(adapter.getById(1L).items()).isEmpty();
    }

    @Test
    void getByIdNotFoundFalla() {
        when(client.findById(99L)).thenThrow(notFound());

        assertThatThrownBy(() -> adapter.getById(99L)).isInstanceOf(RecepcionNotFoundException.class);
    }

    @Test
    void getByIdCaidaSeTraduce() {
        when(client.findById(1L)).thenThrow(down());

        assertThatThrownBy(() -> adapter.getById(1L)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void porFormalizacionMapea() {
        when(client.porFormalizacion(50L)).thenReturn(pendienteResponse());

        assertThat(adapter.porFormalizacion(50L)).isPresent();
    }

    @Test
    void porFormalizacionNotFoundDevuelveVacio() {
        when(client.porFormalizacion(99L)).thenThrow(notFound());

        assertThat(adapter.porFormalizacion(99L)).isEmpty();
    }

    @Test
    void porFormalizacionCaidaSeTraduce() {
        when(client.porFormalizacion(50L)).thenThrow(down());

        assertThatThrownBy(() -> adapter.porFormalizacion(50L))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void registrarMapeaYEnviaElActor() {
        when(client.registrar(any())).thenReturn(response());

        var recepcion = adapter.registrar(solicitud(), 38L);

        assertThat(recepcion.numero()).isEqualTo("RC-2026-000001");
        ArgumentCaptor<CoreCompraRecepcionRequest> captor = ArgumentCaptor.forClass(CoreCompraRecepcionRequest.class);
        verify(client).registrar(captor.capture());
        assertThat(captor.getValue().compraFormalizacionId()).isEqualTo(50L);
        assertThat(captor.getValue().usuarioId()).isEqualTo(38);
        assertThat(captor.getValue().items()).hasSize(1);
        assertThat(captor.getValue().items().get(0).cantidadRecibida()).isEqualByComparingTo("2.00");
    }

    @Test
    void registrarSinActorNiItemsEnviaEstructuraVacia() {
        when(client.registrar(any())).thenReturn(response());

        adapter.registrar(new RecepcionSolicitud(50L, null, null, null, null, null), null);

        ArgumentCaptor<CoreCompraRecepcionRequest> captor = ArgumentCaptor.forClass(CoreCompraRecepcionRequest.class);
        verify(client).registrar(captor.capture());
        assertThat(captor.getValue().usuarioId()).isNull();
        assertThat(captor.getValue().items()).isEmpty();
    }

    @Test
    void registrarNotFoundSeTraduce() {
        doThrow(notFound()).when(client).registrar(any());

        assertThatThrownBy(() -> adapter.registrar(solicitud(), 38L))
                .isInstanceOf(RecepcionNotFoundException.class);
    }

    @Test
    void registrarBadRequestSeTraduce() {
        doThrow(bad()).when(client).registrar(any());

        assertThatThrownBy(() -> adapter.registrar(solicitud(), 38L)).isInstanceOf(RecepcionInvalidaException.class);
    }

    @Test
    void registrarConflictoSeTraduce() {
        doThrow(conflict()).when(client).registrar(any());

        assertThatThrownBy(() -> adapter.registrar(solicitud(), 38L)).isInstanceOf(RecepcionConflictoException.class);
    }

    @Test
    void registrarCaidaSeTraduce() {
        doThrow(down()).when(client).registrar(any());

        assertThatThrownBy(() -> adapter.registrar(solicitud(), 38L))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void cerrarEnviaElMotivoYElActor() {
        assertThatCode(() -> adapter.cerrar(50L, "motivo", 38L)).doesNotThrowAnyException();

        ArgumentCaptor<CoreCerrarCompraRecepcionRequest> captor =
                ArgumentCaptor.forClass(CoreCerrarCompraRecepcionRequest.class);
        verify(client).cerrar(eq(50L), captor.capture());
        assertThat(captor.getValue().motivo()).isEqualTo("motivo");
        assertThat(captor.getValue().usuarioId()).isEqualTo(38);
    }

    @Test
    void cerrarSinActorEnviaUsuarioNulo() {
        adapter.cerrar(50L, "motivo", null);

        ArgumentCaptor<CoreCerrarCompraRecepcionRequest> captor =
                ArgumentCaptor.forClass(CoreCerrarCompraRecepcionRequest.class);
        verify(client).cerrar(eq(50L), captor.capture());
        assertThat(captor.getValue().usuarioId()).isNull();
    }

    @Test
    void cerrarNotFoundSeTraduce() {
        doThrow(notFound()).when(client).cerrar(eq(50L), any());

        assertThatThrownBy(() -> adapter.cerrar(50L, "motivo", 38L))
                .isInstanceOf(RecepcionNotFoundException.class);
    }

    @Test
    void cerrarBadRequestSeTraduce() {
        doThrow(bad()).when(client).cerrar(eq(50L), any());

        assertThatThrownBy(() -> adapter.cerrar(50L, null, 38L)).isInstanceOf(RecepcionInvalidaException.class);
    }

    @Test
    void cerrarConflictoSeTraduce() {
        doThrow(conflict()).when(client).cerrar(eq(50L), any());

        assertThatThrownBy(() -> adapter.cerrar(50L, "motivo", 38L))
                .isInstanceOf(RecepcionConflictoException.class);
    }

    @Test
    void cerrarCaidaSeTraduce() {
        doThrow(down()).when(client).cerrar(eq(50L), any());

        assertThatThrownBy(() -> adapter.cerrar(50L, "motivo", 38L))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }
}
