package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.PedidoCompraFixture;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraEstadoInvalidoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreCompraPedidoFeignClientAdapterTest {

    @Mock
    private CoreCompraPedidoFeignClient coreCompraPedidoFeignClient;

    @InjectMocks
    private CoreCompraPedidoFeignClientAdapter adapter;

    @Test
    void mapsCreateResponseToDomain() {
        when(coreCompraPedidoFeignClient.crear(PedidoCompraFixture.pedidoSinIdentidad()))
                .thenReturn(PedidoCompraFixture.coreResponse());

        var pedido = adapter.crear(PedidoCompraFixture.pedidoSinIdentidad());

        assertThat(pedido.compraPedidoId()).isEqualTo(1);
        assertThat(pedido.items()).hasSize(1);
        assertThat(pedido.items().get(0).descripcion()).isEqualTo("Notebook");
    }

    @Test
    void mapsCreateUnavailable() {
        when(coreCompraPedidoFeignClient.crear(PedidoCompraFixture.pedidoSinIdentidad()))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.crear(PedidoCompraFixture.pedidoSinIdentidad()))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void mapsUpdateResponseToDomain() {
        when(coreCompraPedidoFeignClient.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenReturn(PedidoCompraFixture.coreResponse());

        assertThat(adapter.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()).numero()).isEqualTo("PC-2026-000001");
    }

    @Test
    void mapsUpdateNotFound() {
        when(coreCompraPedidoFeignClient.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenThrow(new FeignException.NotFound("nf", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .isInstanceOf(PedidoCompraNotFoundException.class);
    }

    @Test
    void mapsUpdateConflict() {
        when(coreCompraPedidoFeignClient.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenThrow(new FeignException.Conflict("conflict", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .isInstanceOf(PedidoCompraEstadoInvalidoException.class);
    }

    @Test
    void mapsUpdateUnavailable() {
        when(coreCompraPedidoFeignClient.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void mapsSendResponseToDomain() {
        when(coreCompraPedidoFeignClient.enviar(1, 10)).thenReturn(PedidoCompraFixture.coreResponse());

        assertThat(adapter.enviar(1, 10L).numero()).isEqualTo("PC-2026-000001");
    }

    @Test
    void mapsSendNotFound() {
        when(coreCompraPedidoFeignClient.enviar(1, 10))
                .thenThrow(new FeignException.NotFound("nf", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.enviar(1, 10L)).isInstanceOf(PedidoCompraNotFoundException.class);
    }

    @Test
    void mapsSendConflict() {
        when(coreCompraPedidoFeignClient.enviar(1, 10))
                .thenThrow(new FeignException.Conflict("conflict", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.enviar(1, 10L)).isInstanceOf(PedidoCompraEstadoInvalidoException.class);
    }

    @Test
    void mapsSendUnavailable() {
        when(coreCompraPedidoFeignClient.enviar(1, 10))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.enviar(1, 10L)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void mapsAprobarResponseToDomain() {
        when(coreCompraPedidoFeignClient.aprobar(1, new CoreAprobarCompraPedidoRequest(10)))
                .thenReturn(PedidoCompraFixture.coreResponse());

        assertThat(adapter.aprobar(1, 10L).compraPedidoId()).isEqualTo(1);
    }

    @Test
    void mapsAprobarConflict() {
        when(coreCompraPedidoFeignClient.aprobar(1, new CoreAprobarCompraPedidoRequest(10)))
                .thenThrow(new FeignException.Conflict("conflict", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.aprobar(1, 10L)).isInstanceOf(PedidoCompraEstadoInvalidoException.class);
    }

    @Test
    void mapsRechazarResponseToDomain() {
        when(coreCompraPedidoFeignClient.rechazar(1, new CoreRechazarCompraPedidoRequest(10, "motivo")))
                .thenReturn(PedidoCompraFixture.coreResponse());

        assertThat(adapter.rechazar(1, 10L, "motivo").compraPedidoId()).isEqualTo(1);
    }

    @Test
    void mapsRechazarNotFound() {
        when(coreCompraPedidoFeignClient.rechazar(1, new CoreRechazarCompraPedidoRequest(10, "motivo")))
                .thenThrow(new FeignException.NotFound("nf", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.rechazar(1, 10L, "motivo"))
                .isInstanceOf(PedidoCompraNotFoundException.class);
    }

    @Test
    void mapsDescartarResponseToDomain() {
        when(coreCompraPedidoFeignClient.descartar(1, new CoreDescartarCompraPedidoRequest(10, "motivo")))
                .thenReturn(PedidoCompraFixture.coreResponse());

        assertThat(adapter.descartar(1, 10L, "motivo").compraPedidoId()).isEqualTo(1);
    }

    @Test
    void mapsDescartarUnavailable() {
        when(coreCompraPedidoFeignClient.descartar(1, new CoreDescartarCompraPedidoRequest(10, "motivo")))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.descartar(1, 10L, "motivo"))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void mapswithNullUsuarioId() {
        when(coreCompraPedidoFeignClient.enviar(1, null)).thenReturn(PedidoCompraFixture.coreResponse());

        assertThat(adapter.enviar(1, null).compraPedidoId()).isEqualTo(1);
    }

    @Test
    void mapsGetByIdResponseToDomain() {
        when(coreCompraPedidoFeignClient.getById(1)).thenReturn(PedidoCompraFixture.coreResponse());

        assertThat(adapter.getById(1).compraPedidoId()).isEqualTo(1);
    }

    @Test
    void mapsGetByIdNotFound() {
        when(coreCompraPedidoFeignClient.getById(1))
                .thenThrow(new FeignException.NotFound("nf", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.getById(1)).isInstanceOf(PedidoCompraNotFoundException.class);
    }

    @Test
    void mapsGetByIdUnavailable() {
        when(coreCompraPedidoFeignClient.getById(1))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.getById(1)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void listsBySolicitante() {
        when(coreCompraPedidoFeignClient.listarPorSolicitante(10L)).thenReturn(List.of(PedidoCompraFixture.coreResponse()));

        assertThat(adapter.listarPorSolicitante(10L)).hasSize(1);
    }

    @Test
    void mapsListWithNullItems() {
        var response = new CoreCompraPedidoResponse(1, null, null, null, "BORRADOR", null, 10, 20, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(coreCompraPedidoFeignClient.listarPorSolicitante(10L)).thenReturn(List.of(response));

        assertThat(adapter.listarPorSolicitante(10L).get(0).items()).isEmpty();
    }

    @Test
    void mapsListUnavailable() {
        when(coreCompraPedidoFeignClient.listarPorSolicitante(10L))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.listarPorSolicitante(10L))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void listsByFiltro() {
        PedidoCompraFiltro filtro = new PedidoCompraFiltro("PENDIENTE_ENVIO", null, null,
                List.of(20, 21), null, null);
        when(coreCompraPedidoFeignClient.search(new CoreCompraPedidoSearchRequest(
                "PENDIENTE_ENVIO", null, null, List.of(20, 21), null, null)))
                .thenReturn(List.of(PedidoCompraFixture.coreResponse()));

        assertThat(adapter.listar(filtro)).hasSize(1);
    }

    @Test
    void mapsListByFiltroUnavailable() {
        PedidoCompraFiltro filtro = new PedidoCompraFiltro(null, null, null, null, null, null);
        when(coreCompraPedidoFeignClient.search(new CoreCompraPedidoSearchRequest(null, null, null, null, null, null)))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.listar(filtro)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    private Request request() {
        return Request.create(Request.HttpMethod.GET, "http://core-service.test/api/tesoreria/core/compraPedido/1",
                Map.of(), null, StandardCharsets.UTF_8);
    }
}
