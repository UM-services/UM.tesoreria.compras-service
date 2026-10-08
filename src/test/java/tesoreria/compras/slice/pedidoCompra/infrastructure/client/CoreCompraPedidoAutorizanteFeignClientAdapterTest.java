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
class CoreCompraPedidoAutorizanteFeignClientAdapterTest {

    @Mock
    private CoreCompraPedidoAutorizanteFeignClient coreCompraPedidoAutorizanteFeignClient;

    @InjectMocks
    private CoreCompraPedidoAutorizanteFeignClientAdapter adapter;

    @Test
    void devuelveLasDependenciasAutorizadas() {
        when(coreCompraPedidoAutorizanteFeignClient.getDependencias(10L))
                .thenReturn(PedidoCompraFixture.coreAutorizanteResponse(List.of(20, 21)));

        assertThat(adapter.getDependenciasAutorizadas(10L)).containsExactly(20, 21);
    }

    @Test
    void listaNulaDevuelveListaVacia() {
        when(coreCompraPedidoAutorizanteFeignClient.getDependencias(10L))
                .thenReturn(PedidoCompraFixture.coreAutorizanteResponse(null));

        assertThat(adapter.getDependenciasAutorizadas(10L)).isEmpty();
    }

    @Test
    void notFoundDevuelveListaVacia() {
        when(coreCompraPedidoAutorizanteFeignClient.getDependencias(10L))
                .thenThrow(new FeignException.NotFound("nf", request(), new byte[0], Map.of()));

        assertThat(adapter.getDependenciasAutorizadas(10L)).isEmpty();
    }

    @Test
    void unavailableLanzaSourceUnavailable() {
        when(coreCompraPedidoAutorizanteFeignClient.getDependencias(10L))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.getDependenciasAutorizadas(10L))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    private Request request() {
        return Request.create(Request.HttpMethod.GET,
                "http://core-service.test/api/tesoreria/core/compraPedidoAutorizante/dependencias/10",
                Map.of(), null, StandardCharsets.UTF_8);
    }
}
