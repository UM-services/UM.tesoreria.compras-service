package tesoreria.compras.slice.proveedor.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.proveedor.ProveedorFixture;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorNotFoundException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorSourceUnavailableException;

import java.nio.charset.StandardCharsets;
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
