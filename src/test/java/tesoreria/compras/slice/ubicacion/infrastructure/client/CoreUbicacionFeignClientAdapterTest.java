package tesoreria.compras.slice.ubicacion.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.ubicacion.UbicacionFixture;
import tesoreria.compras.slice.ubicacion.domain.exception.UbicacionSourceUnavailableException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreUbicacionFeignClientAdapterTest {

    @Mock
    private CoreUbicacionFeignClient coreUbicacionFeignClient;

    @InjectMocks
    private CoreUbicacionFeignClientAdapter adapter;

    @Test
    void mapsUbicaciones() {
        when(coreUbicacionFeignClient.getUbicaciones()).thenReturn(List.of(UbicacionFixture.coreUbicacionResponse()));

        var result = adapter.getUbicaciones();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().ubicacionId()).isEqualTo(1);
        verify(coreUbicacionFeignClient).getUbicaciones();
    }

    @Test
    void translatesUnavailableCore() {
        when(coreUbicacionFeignClient.getUbicaciones()).thenThrow(new FeignException.ServiceUnavailable(
                "core unavailable", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.getUbicaciones())
                .isInstanceOf(UbicacionSourceUnavailableException.class)
                .hasMessage("La fuente de ubicaciones no está disponible");
    }

    private Request request() {
        return Request.create(Request.HttpMethod.GET,
                "http://core-service.test/api/tesoreria/core/ubicacion/",
                Map.of(), null, StandardCharsets.UTF_8);
    }
}
