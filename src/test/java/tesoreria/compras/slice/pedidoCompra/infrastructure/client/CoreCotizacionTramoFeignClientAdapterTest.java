package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionTramoConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionTramoInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionTramoNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionesNoConfiguradasException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreCotizacionTramoFeignClientAdapterTest {

    @Mock
    private CoreCotizacionTramoFeignClient client;

    @InjectMocks
    private CoreCotizacionTramoFeignClientAdapter adapter;

    private CotizacionTramo tramo() {
        return new CotizacionTramo(1L, "N1", 2, 1, false, (byte) 1);
    }

    private CoreCotizacionTramoResponse response() {
        return new CoreCotizacionTramoResponse(1L, "N1", 2, 1, false, (byte) 1);
    }

    private Request request() {
        return Request.create(Request.HttpMethod.GET, "http://core-service.test/x", Map.of(), null,
                StandardCharsets.UTF_8);
    }

    @Test
    void listarMapea() {
        when(client.findAll()).thenReturn(List.of(response()));

        List<CotizacionTramo> tramos = adapter.listar();

        assertThat(tramos).hasSize(1);
        assertThat(tramos.get(0).cotizacionTramoId()).isEqualTo(1L);
        assertThat(tramos.get(0).topeMultiplico()).isEqualTo(2);
    }

    @Test
    void listarUnavailable() {
        when(client.findAll()).thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.listar())
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void crearMapea() {
        when(client.create(any())).thenReturn(response());

        assertThat(adapter.crear(tramo()).cotizacionTramoId()).isEqualTo(1L);
    }

    @Test
    void crearInvalido() {
        when(client.create(any())).thenThrow(new FeignException.BadRequest("bad", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.crear(tramo()))
                .isInstanceOf(CotizacionTramoInvalidaException.class);
    }

    @Test
    void crearConflicto() {
        when(client.create(any())).thenThrow(new FeignException.Conflict("conflict", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.crear(tramo()))
                .isInstanceOf(CotizacionTramoConflictoException.class);
    }

    @Test
    void crearUnavailable() {
        when(client.create(any())).thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.crear(tramo()))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void actualizarMapea() {
        when(client.update(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(response());

        assertThat(adapter.actualizar(1L, tramo()).cantidadMinima()).isEqualTo(1);
    }

    @Test
    void actualizarNotFound() {
        when(client.update(org.mockito.ArgumentMatchers.eq(1L), any()))
                .thenThrow(new FeignException.NotFound("nf", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.actualizar(1L, tramo()))
                .isInstanceOf(CotizacionTramoNotFoundException.class);
    }

    @Test
    void actualizarConflicto() {
        when(client.update(org.mockito.ArgumentMatchers.eq(1L), any()))
                .thenThrow(new FeignException.Conflict("conflict", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.actualizar(1L, tramo()))
                .isInstanceOf(CotizacionTramoConflictoException.class);
    }

    @Test
    void eliminarOk() {
        adapter.eliminar(1L);

        verify(client).delete(1L);
    }

    @Test
    void eliminarNotFound() {
        org.mockito.Mockito.doThrow(new FeignException.NotFound("nf", request(), new byte[0], Map.of()))
                .when(client).delete(1L);

        assertThatThrownBy(() -> adapter.eliminar(1L))
                .isInstanceOf(CotizacionTramoNotFoundException.class);
    }

    @Test
    void eliminarConflicto() {
        org.mockito.Mockito.doThrow(new FeignException.Conflict("conflict", request(), new byte[0], Map.of()))
                .when(client).delete(1L);

        assertThatThrownBy(() -> adapter.eliminar(1L))
                .isInstanceOf(CotizacionTramoConflictoException.class);
    }

    @Test
    void resolverMinimoMapea() {
        when(client.minimo(15, new BigDecimal("150000")))
                .thenReturn(new CoreCotizacionMinimoResponse(2));

        assertThat(adapter.resolverMinimo(15, new BigDecimal("150000"))).isEqualTo(2);
    }

    @Test
    void resolverMinimoNoConfigurado() {
        when(client.minimo(15, new BigDecimal("150000")))
                .thenThrow(new FeignException.Conflict("no config", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.resolverMinimo(15, new BigDecimal("150000")))
                .isInstanceOf(CotizacionesNoConfiguradasException.class);
    }

    @Test
    void resolverMinimoUnavailable() {
        when(client.minimo(15, new BigDecimal("150000")))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.resolverMinimo(15, new BigDecimal("150000")))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }
}
