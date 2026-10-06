package tesoreria.compras.slice.articulo.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloNotFoundException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloSourceUnavailableException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreArticuloFeignClientAdapterTest {

    @Mock
    private CoreArticuloFeignClient coreArticuloFeignClient;

    @InjectMocks
    private CoreArticuloFeignClientAdapter adapter;

    @Test
    void mapsCoreResponseToDomainModel() {
        when(coreArticuloFeignClient.getArticuloById(101L)).thenReturn(ArticuloFixture.coreArticuloResponse());

        var articulo = adapter.getArticuloById(101L);

        assertThat(articulo.articuloId()).isEqualTo(101L);
        assertThat(articulo.nombre()).isEqualTo("Resma A4");
        assertThat(articulo.habilitado()).isEqualTo((byte) 1);
        assertThat(articulo.cuenta().cuentaContableId()).isEqualTo(2133L);
        verify(coreArticuloFeignClient).getArticuloById(101L);
    }

    @Test
    void mapsArticuloWithoutCuenta() {
        var response = new CoreArticuloResponse(
                102L, "Sin cuenta", "", "", null, (byte) 0, null, null, "", (byte) 0, (byte) 0, null
        );
        when(coreArticuloFeignClient.getArticuloById(102L)).thenReturn(response);

        var articulo = adapter.getArticuloById(102L);

        assertThat(articulo.cuenta()).isNull();
        assertThat(articulo.habilitado()).isZero();
    }

    @Test
    void mapsSearchResultsToDomainModel() {
        var conditions = List.of("resma");
        when(coreArticuloFeignClient.searchArticulos(conditions))
                .thenReturn(List.of(ArticuloFixture.coreArticuloSearchResponse()));

        var articulos = adapter.searchArticulos(conditions);

        assertThat(articulos).hasSize(1);
        assertThat(articulos.getFirst().nombre()).isEqualTo("Resma A4");
        assertThat(articulos.getFirst().cuenta().nombre()).isEqualTo("Obligaciones a Pagar");
        verify(coreArticuloFeignClient).searchArticulos(conditions);
    }

    @Test
    void translatesCoreNotFoundWithoutLeakingFeign() {
        when(coreArticuloFeignClient.getArticuloById(101L)).thenThrow(new FeignException.NotFound(
                "not found", request(), new byte[0], Map.of()
        ));

        assertThatThrownBy(() -> adapter.getArticuloById(101L))
                .isInstanceOf(ArticuloNotFoundException.class)
                .hasMessage("No existe el artículo con ID 101");
    }

    @Test
    void translatesUnavailableCoreWithoutLeakingFeign() {
        when(coreArticuloFeignClient.getArticuloById(101L)).thenThrow(new FeignException.ServiceUnavailable(
                "core unavailable", request(), new byte[0], Map.of()
        ));

        assertThatThrownBy(() -> adapter.getArticuloById(101L))
                .isInstanceOf(ArticuloSourceUnavailableException.class)
                .hasMessage("La fuente de artículos no está disponible");
    }

    @Test
    void translatesUnavailableSearchWithoutLeakingFeign() {
        var conditions = List.of("resma");
        when(coreArticuloFeignClient.searchArticulos(conditions)).thenThrow(new FeignException.ServiceUnavailable(
                "core unavailable", request(), new byte[0], Map.of()
        ));

        assertThatThrownBy(() -> adapter.searchArticulos(conditions))
                .isInstanceOf(ArticuloSourceUnavailableException.class)
                .hasMessage("La fuente de artículos no está disponible");
    }

    private Request request() {
        return Request.create(
                Request.HttpMethod.GET,
                "http://core-service.test/api/tesoreria/core/articulo/101",
                Map.of(),
                null,
                StandardCharsets.UTF_8
        );
    }
}
