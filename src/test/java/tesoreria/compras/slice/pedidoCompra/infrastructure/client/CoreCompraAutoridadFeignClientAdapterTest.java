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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreCompraAutoridadFeignClientAdapterTest {

    @Mock
    private CoreCompraAutoridadFeignClient coreCompraAutoridadFeignClient;

    @InjectMocks
    private CoreCompraAutoridadFeignClientAdapter adapter;

    @Test
    void mapsLimiteToDomain() {
        when(coreCompraAutoridadFeignClient.getLimite(10, 7))
                .thenReturn(PedidoCompraFixture.coreLimiteResponse(new BigDecimal("4500000.00")));

        var limite = adapter.getLimite(10L, 7);

        assertThat(limite.usuarioId()).isEqualTo(10L);
        assertThat(limite.multiplico()).isEqualTo(3);
        assertThat(limite.limite()).isEqualByComparingTo("4500000.00");
        assertThat(limite.tieneAutoridad()).isTrue();
        assertThat(limite.ilimitado()).isFalse();
    }

    @Test
    void mapsNullUsuarioId() {
        var response = new CoreLimiteAutorizacionResponse(null, 7, null,
                null, null, true, true);
        when(coreCompraAutoridadFeignClient.getLimite(null, 7)).thenReturn(response);

        var limite = adapter.getLimite(null, 7);

        assertThat(limite.usuarioId()).isNull();
        assertThat(limite.ilimitado()).isTrue();
    }

    @Test
    void mapsNullResponse() {
        when(coreCompraAutoridadFeignClient.getLimite(10, 7)).thenReturn(null);

        assertThat(adapter.getLimite(10L, 7)).isNull();
    }

    @Test
    void mapsUnavailable() {
        when(coreCompraAutoridadFeignClient.getLimite(10, 7))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.getLimite(10L, 7))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    private Request request() {
        return Request.create(Request.HttpMethod.GET,
                "http://core-service.test/api/tesoreria/core/compraAutoridadUsuario/limite/10/7",
                Map.of(), null, StandardCharsets.UTF_8);
    }
}
