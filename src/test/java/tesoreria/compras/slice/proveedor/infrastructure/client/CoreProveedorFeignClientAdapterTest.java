package tesoreria.compras.slice.proveedor.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.model.PageRequest;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.ProveedorFixture;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorConflictException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorCuitNotFoundException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorNotFoundException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorSourceUnavailableException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorValidationException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreProveedorFeignClientAdapterTest {

    @Mock
    private CoreProveedorFeignClient coreProveedorFeignClient;

    @InjectMocks
    private CoreProveedorFeignClientAdapter adapter;

    @Test
    void mapsCoreResponseToDomainModel() {
        when(coreProveedorFeignClient.getProveedorById(8)).thenReturn(ProveedorFixture.coreProveedorResponse());

        var proveedor = adapter.getProveedorById(8);

        assertThat(proveedor.proveedorId()).isEqualTo(8);
        assertThat(proveedor.razonSocial()).isEqualTo("Proveedor de Prueba");
        assertThat(proveedor.habilitado()).isEqualTo(1);
        assertThat(proveedor.cuenta().cuentaContableId()).isEqualTo(2133);
        verify(coreProveedorFeignClient).getProveedorById(8);
    }

    @Test
    void mapsProveedorWithoutCuenta() {
        var response = new CoreProveedorResponse(
                9, "", "", "Sin cuenta", "", "", "", "", "", "", "", null, 0, "", null
        );
        when(coreProveedorFeignClient.getProveedorById(9)).thenReturn(response);

        var proveedor = adapter.getProveedorById(9);

        assertThat(proveedor.cuenta()).isNull();
        assertThat(proveedor.habilitado()).isZero();
    }

    @Test
    void mapsPaginated() {
        var page = new PaginatedResponse<>(List.of(ProveedorFixture.coreProveedorResponse()), 1L, 1, 0, 20);
        when(coreProveedorFeignClient.getPaginated(new PageRequest(0, 20))).thenReturn(page);

        var result = adapter.getPaginated(0, 20);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data().getFirst().razonSocial()).isEqualTo("Proveedor de Prueba");
        assertThat(result.totalElements()).isEqualTo(1L);
    }

    @Test
    void mapsPaginatedWithNullData() {
        var page = new PaginatedResponse<CoreProveedorResponse>(null, 0L, 0, 0, 20);
        when(coreProveedorFeignClient.getPaginated(new PageRequest(0, 20))).thenReturn(page);

        assertThat(adapter.getPaginated(0, 20).data()).isEmpty();
    }

    @Test
    void mapsSearchResults() {
        var conditions = List.of("acme");
        when(coreProveedorFeignClient.search(conditions)).thenReturn(List.of(ProveedorFixture.coreProveedorResponse()));

        var result = adapter.search(conditions);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().proveedorId()).isEqualTo(8);
    }

    @Test
    void mapsByCuit() {
        when(coreProveedorFeignClient.getByCuit("20-10564397-8")).thenReturn(ProveedorFixture.coreProveedorResponse());

        assertThat(adapter.getByCuit("20-10564397-8").proveedorId()).isEqualTo(8);
    }

    @Test
    void translatesCuitNotFound() {
        when(coreProveedorFeignClient.getByCuit("20-1-1"))
                .thenThrow(new FeignException.NotFound("not found", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.getByCuit("20-1-1"))
                .isInstanceOf(ProveedorCuitNotFoundException.class)
                .hasMessage("No existe el proveedor con CUIT 20-1-1");
    }

    @Test
    void creates() {
        var proveedor = ProveedorFixture.proveedor();
        when(coreProveedorFeignClient.create(proveedor)).thenReturn(ProveedorFixture.coreProveedorResponse());

        assertThat(adapter.create(proveedor).proveedorId()).isEqualTo(8);
    }

    @Test
    void updates() {
        var proveedor = ProveedorFixture.proveedor();
        when(coreProveedorFeignClient.update(8, proveedor)).thenReturn(ProveedorFixture.coreProveedorResponse());

        assertThat(adapter.update(8, proveedor).proveedorId()).isEqualTo(8);
    }

    @Test
    void deletes() {
        adapter.delete(8);

        verify(coreProveedorFeignClient).delete(8);
    }

    @Test
    void translatesConflict() {
        var proveedor = ProveedorFixture.proveedor();
        when(coreProveedorFeignClient.create(proveedor))
                .thenThrow(new FeignException.Conflict("conflict", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.create(proveedor)).isInstanceOf(ProveedorConflictException.class);
    }

    @Test
    void translatesValidationError() {
        var proveedor = ProveedorFixture.proveedor();
        when(coreProveedorFeignClient.create(proveedor))
                .thenThrow(new FeignException.BadRequest("bad", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.create(proveedor)).isInstanceOf(ProveedorValidationException.class);
    }

    @Test
    void translatesCoreNotFoundWithoutLeakingFeign() {
        when(coreProveedorFeignClient.getProveedorById(8)).thenThrow(new FeignException.NotFound(
                "not found", request(), new byte[0], Map.of()
        ));

        assertThatThrownBy(() -> adapter.getProveedorById(8))
                .isInstanceOf(ProveedorNotFoundException.class)
                .hasMessage("No existe el proveedor con ID 8");
    }

    @Test
    void translatesUnavailableCoreWithoutLeakingFeign() {
        when(coreProveedorFeignClient.getProveedorById(8)).thenThrow(new FeignException.ServiceUnavailable(
                "core unavailable", request(), new byte[0], Map.of()
        ));

        assertThatThrownBy(() -> adapter.getProveedorById(8))
                .isInstanceOf(ProveedorSourceUnavailableException.class)
                .hasMessage("La fuente de proveedores no está disponible");
    }

    private Request request() {
        return Request.create(
                Request.HttpMethod.GET,
                "http://core-service.test/api/tesoreria/core/proveedor/8",
                Map.of(),
                null,
                StandardCharsets.UTF_8
        );
    }
}
