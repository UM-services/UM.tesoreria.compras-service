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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreEjercicioFeignClientAdapterTest {

    @Mock
    private CoreEjercicioFeignClient coreEjercicioFeignClient;

    @InjectMocks
    private CoreEjercicioFeignClientAdapter adapter;

    @Test
    void mapsEjercicioId() {
        when(coreEjercicioFeignClient.getLast()).thenReturn(new CoreEjercicioResponse(15));

        assertThat(adapter.getEjercicioActualId()).isEqualTo(15);
    }

    @Test
    void mapsNullResponse() {
        when(coreEjercicioFeignClient.getLast()).thenReturn(null);

        assertThat(adapter.getEjercicioActualId()).isNull();
    }

    @Test
    void mapsUnavailable() {
        when(coreEjercicioFeignClient.getLast())
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(adapter::getEjercicioActualId)
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    private Request request() {
        return Request.create(Request.HttpMethod.GET,
                "http://core-service.test/ejercicio/last",
                Map.of(), null, StandardCharsets.UTF_8);
    }
}
