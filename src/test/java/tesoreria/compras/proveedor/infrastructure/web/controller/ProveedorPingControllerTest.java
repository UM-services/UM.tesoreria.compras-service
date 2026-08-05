package tesoreria.compras.proveedor.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.proveedor.ProveedorFixture;
import tesoreria.compras.proveedor.application.service.ProveedorService;
import tesoreria.compras.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProveedorPingControllerTest {

    @Mock
    private ProveedorService proveedorService;

    @Test
    void returnsProveedorFetchedFromCore() {
        when(proveedorService.getProveedorById(8)).thenReturn(ProveedorFixture.proveedor());

        var controller = new ProveedorPingController(proveedorService, new ProveedorDtoMapper());
        var response = controller.ping(8);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().razonSocial()).isEqualTo("Roberto Mario Cerutti");
        verify(proveedorService).getProveedorById(8);
    }
}
