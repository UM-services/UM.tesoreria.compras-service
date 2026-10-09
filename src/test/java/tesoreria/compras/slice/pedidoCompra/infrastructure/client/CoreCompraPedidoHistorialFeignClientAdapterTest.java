package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.PedidoCompraFixture;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreCompraPedidoHistorialFeignClientAdapterTest {

    @Mock
    private CoreCompraPedidoHistorialFeignClient coreCompraPedidoHistorialFeignClient;

    @InjectMocks
    private CoreCompraPedidoHistorialFeignClientAdapter adapter;

    @Test
    void mapeaElHistorial() {
        when(coreCompraPedidoHistorialFeignClient.listar(1))
                .thenReturn(List.of(PedidoCompraFixture.coreHistorialResponse()));

        var historial = adapter.listar(1);

        assertThat(historial).hasSize(1);
        assertThat(historial.get(0).estado()).isEqualTo("ENVIADO");
    }

    @Test
    void unavailableLanzaSourceUnavailable() {
        when(coreCompraPedidoHistorialFeignClient.listar(1))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.listar(1)).isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    private Request request() {
        return Request.create(Request.HttpMethod.GET,
                "http://core-service.test/api/tesoreria/core/compraPedidoHistorial/1",
                Map.of(), null, StandardCharsets.UTF_8);
    }
}
