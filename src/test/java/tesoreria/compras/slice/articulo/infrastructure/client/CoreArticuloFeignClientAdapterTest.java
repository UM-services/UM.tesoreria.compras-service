package tesoreria.compras.slice.articulo.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.model.PageRequest;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloConflictException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloNotFoundException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloSourceUnavailableException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloValidationException;

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
    void mapsPaginatedResults() {
        var page = new PaginatedResponse<>(List.of(ArticuloFixture.coreArticuloResponse()), 1L, 1, 0, 10);
        when(coreArticuloFeignClient.getPaginatedByTipo("gasto", new PageRequest(0, 10))).thenReturn(page);

        var result = adapter.getPaginatedByTipo("gasto", 0, 10);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data().getFirst().nombre()).isEqualTo("Resma A4");
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.currentPage()).isZero();
    }

    @Test
    void mapsPaginatedResultsWithNullData() {
        var page = new PaginatedResponse<CoreArticuloResponse>(null, 0L, 0, 0, 10);
        when(coreArticuloFeignClient.getPaginatedByTipo("gasto", new PageRequest(0, 10))).thenReturn(page);

        var result = adapter.getPaginatedByTipo("gasto", 0, 10);

        assertThat(result.data()).isEmpty();
    }

    @Test
    void mapsNewArticulo() {
        when(coreArticuloFeignClient.getNewArticulo()).thenReturn(ArticuloFixture.coreArticuloResponse());

        assertThat(adapter.getNewArticulo().articuloId()).isEqualTo(101L);
    }

    @Test
    void createsArticulo() {
        var articulo = ArticuloFixture.articulo();
        when(coreArticuloFeignClient.createArticulo(articulo)).thenReturn(ArticuloFixture.coreArticuloResponse());

        assertThat(adapter.createArticulo(articulo).articuloId()).isEqualTo(101L);
        verify(coreArticuloFeignClient).createArticulo(articulo);
    }

    @Test
    void updatesArticulo() {
        var articulo = ArticuloFixture.articulo();
        when(coreArticuloFeignClient.updateArticulo(101L, articulo)).thenReturn(ArticuloFixture.coreArticuloResponse());

        assertThat(adapter.updateArticulo(101L, articulo).articuloId()).isEqualTo(101L);
    }

    @Test
    void deletesArticulo() {
        adapter.deleteArticulo(101L);

        verify(coreArticuloFeignClient).deleteArticulo(101L);
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

    @Test
    void translatesCreateConflict() {
        var articulo = ArticuloFixture.articulo();
        when(coreArticuloFeignClient.createArticulo(articulo)).thenThrow(new FeignException.Conflict(
                "conflict", request(), new byte[0], Map.of()
        ));

        assertThatThrownBy(() -> adapter.createArticulo(articulo))
                .isInstanceOf(ArticuloConflictException.class);
    }

    @Test
    void translatesValidationError() {
        var articulo = ArticuloFixture.articulo();
        when(coreArticuloFeignClient.createArticulo(articulo)).thenThrow(new FeignException.BadRequest(
                "bad request", request(), new byte[0], Map.of()
        ));

        assertThatThrownBy(() -> adapter.createArticulo(articulo))
                .isInstanceOf(ArticuloValidationException.class);
    }

    @Test
    void translatesUpdateNotFound() {
        var articulo = ArticuloFixture.articulo();
        when(coreArticuloFeignClient.updateArticulo(101L, articulo)).thenThrow(new FeignException.NotFound(
                "not found", request(), new byte[0], Map.of()
        ));

        assertThatThrownBy(() -> adapter.updateArticulo(101L, articulo))
                .isInstanceOf(ArticuloNotFoundException.class);
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
