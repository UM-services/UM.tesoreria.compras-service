package tesoreria.compras.slice.ubicacionArticulo.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.ubicacionArticulo.UbicacionArticuloFixture;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloConflictException;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloSourceUnavailableException;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloValidationException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreUbicacionArticuloFeignClientAdapterTest {

    @Mock
    private CoreUbicacionArticuloFeignClient coreUbicacionArticuloFeignClient;

    @InjectMocks
    private CoreUbicacionArticuloFeignClientAdapter adapter;

    @Test
    void mapsByArticulo() {
        when(coreUbicacionArticuloFeignClient.getByArticulo(101L))
                .thenReturn(List.of(UbicacionArticuloFixture.coreResponse()));

        var result = adapter.getByArticulo(101L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().ubicacionNombre()).isEqualTo("Rectorado");
        assertThat(result.getFirst().cuentaNombre()).isEqualTo("Obligaciones a Pagar");
        verify(coreUbicacionArticuloFeignClient).getByArticulo(101L);
    }

    @Test
    void mapsSave() {
        var vinculado = UbicacionArticuloFixture.ubicacionArticulo();
        when(coreUbicacionArticuloFeignClient.save(vinculado)).thenReturn(UbicacionArticuloFixture.coreResponse());

        assertThat(adapter.save(vinculado).ubicacionArticuloId()).isEqualTo(50L);
    }

    @Test
    void mapsNullRefs() {
        when(coreUbicacionArticuloFeignClient.getByArticulo(1L)).thenReturn(List.of(
                new CoreUbicacionArticuloResponse(50L, 1, 101L, null, null, null)));

        var result = adapter.getByArticulo(1L);

        assertThat(result.getFirst().ubicacionNombre()).isNull();
        assertThat(result.getFirst().cuentaNombre()).isNull();
    }

    @Test
    void translatesConflict() {
        var vinculado = UbicacionArticuloFixture.ubicacionArticulo();
        when(coreUbicacionArticuloFeignClient.save(vinculado))
                .thenThrow(new FeignException.Conflict("conflict", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.save(vinculado)).isInstanceOf(UbicacionArticuloConflictException.class);
    }

    @Test
    void translatesValidationError() {
        var vinculado = UbicacionArticuloFixture.ubicacionArticulo();
        when(coreUbicacionArticuloFeignClient.save(vinculado))
                .thenThrow(new FeignException.BadRequest("bad", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.save(vinculado)).isInstanceOf(UbicacionArticuloValidationException.class);
    }

    @Test
    void translatesUnavailable() {
        when(coreUbicacionArticuloFeignClient.getByArticulo(101L))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.getByArticulo(101L))
                .isInstanceOf(UbicacionArticuloSourceUnavailableException.class)
                .hasMessage("La fuente de imputaciones no está disponible");
    }

    private Request request() {
        return Request.create(Request.HttpMethod.POST,
                "http://core-service.test/api/tesoreria/core/ubicacionArticulo/",
                Map.of(), null, StandardCharsets.UTF_8);
    }
}
