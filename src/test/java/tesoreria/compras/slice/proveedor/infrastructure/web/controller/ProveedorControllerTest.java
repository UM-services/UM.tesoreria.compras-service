package tesoreria.compras.slice.proveedor.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.model.PageRequest;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.ProveedorFixture;
import tesoreria.compras.slice.proveedor.application.service.ProveedorService;
import tesoreria.compras.slice.proveedor.infrastructure.web.dto.ProveedorRequest;
import tesoreria.compras.slice.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProveedorControllerTest {

    @Mock
    private ProveedorService proveedorService;

    private ProveedorController controller() {
        return new ProveedorController(proveedorService, new ProveedorDtoMapper());
    }

    @Test
    void returnsPaginated() {
        var page = new PaginatedResponse<>(List.of(ProveedorFixture.proveedor()), 1L, 1, 0, 20);
        when(proveedorService.getPaginated(0, 20)).thenReturn(page);

        var response = controller().findPaginated(new PageRequest(0, 20));

        assertThat(response.getBody().data()).hasSize(1);
        assertThat(response.getBody().totalElements()).isEqualTo(1L);
    }

    @Test
    void returnsSearchResults() {
        var conditions = List.of("acme");
        when(proveedorService.search(conditions)).thenReturn(List.of(ProveedorFixture.proveedor()));

        var response = controller().search(conditions);

        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().razonSocial()).isEqualTo("Proveedor de Prueba");
    }

    @Test
    void returnsByCuit() {
        when(proveedorService.getByCuit("20-10564397-8")).thenReturn(ProveedorFixture.proveedor());

        var response = controller().findByCuit("20-10564397-8");

        assertThat(response.getBody().proveedorId()).isEqualTo(8);
    }

    @Test
    void returnsById() {
        when(proveedorService.getProveedorById(8)).thenReturn(ProveedorFixture.proveedor());

        assertThat(controller().findById(8).getBody().cuit()).isEqualTo("20-10564397-8");
    }

    @Test
    void creates() {
        when(proveedorService.create(any())).thenReturn(ProveedorFixture.proveedor());

        var response = controller().create(request());

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().razonSocial()).isEqualTo("Proveedor de Prueba");
    }

    @Test
    void updates() {
        when(proveedorService.update(eq(8), any())).thenReturn(ProveedorFixture.proveedor());

        assertThat(controller().update(8, request()).getBody().proveedorId()).isEqualTo(8);
    }

    @Test
    void deletes() {
        var response = controller().delete(8);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(proveedorService).delete(8);
    }

    private ProveedorRequest request() {
        return new ProveedorRequest(
                "20-10564397-8", "", "Proveedor de Prueba", "", "", "", "", "", "", "",
                20101090099L, (byte) 1, "");
    }
}
