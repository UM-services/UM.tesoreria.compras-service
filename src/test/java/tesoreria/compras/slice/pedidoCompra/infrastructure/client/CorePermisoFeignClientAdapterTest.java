package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorePermisoFeignClientAdapterTest {

    @Mock
    private CorePermisoFeignClient corePermisoFeignClient;

    @InjectMocks
    private CorePermisoFeignClientAdapter adapter;

    @Test
    void returnsBundle() {
        when(corePermisoFeignClient.getPermisosEfectivos(10L))
                .thenReturn(new CorePermisoEfectivoResponse(10L, List.of("compras.iniciar_pedido")));

        assertThat(adapter.getPermisosEfectivos(10L)).containsExactly("compras.iniciar_pedido");
    }

    @Test
    void nullBundleReturnsEmpty() {
        when(corePermisoFeignClient.getPermisosEfectivos(10L))
                .thenReturn(new CorePermisoEfectivoResponse(10L, null));

        assertThat(adapter.getPermisosEfectivos(10L)).isEmpty();
    }

    @Test
    void unknownUserReturnsEmptyFailClosed() {
        when(corePermisoFeignClient.getPermisosEfectivos(10L))
                .thenThrow(new FeignException.NotFound("nf", request(), new byte[0], Map.of()));

        assertThat(adapter.getPermisosEfectivos(10L)).isEmpty();
    }

    @Test
    void unavailableThrowsSourceUnavailable() {
        when(corePermisoFeignClient.getPermisosEfectivos(10L))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.getPermisosEfectivos(10L))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    private Request request() {
        return Request.create(Request.HttpMethod.GET,
                "http://core-service.test/api/tesoreria/core/permisoEfectivo/usuario/10",
                Map.of(), null, StandardCharsets.UTF_8);
    }
}
