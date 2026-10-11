package tesoreria.compras.slice.proveedor.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.ProveedorFixture;
import tesoreria.compras.slice.proveedor.domain.ports.out.ProveedorGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProveedorUseCasesTest {

    @Mock
    private ProveedorGateway proveedorGateway;

    @Test
    void getPaginatedDelegates() {
        var page = new PaginatedResponse<>(List.of(ProveedorFixture.proveedor()), 1L, 1, 0, 20);
        when(proveedorGateway.getPaginated(0, 20)).thenReturn(page);

        assertThat(new GetPaginatedProveedoresUseCaseImpl(proveedorGateway).getPaginated(0, 20)).isSameAs(page);
    }

    @Test
    void searchDelegates() {
        var conditions = List.of("acme");
        when(proveedorGateway.search(conditions)).thenReturn(List.of(ProveedorFixture.proveedor()));

        assertThat(new SearchProveedoresUseCaseImpl(proveedorGateway).search(conditions)).hasSize(1);
    }

    @Test
    void getByCuitDelegates() {
        when(proveedorGateway.getByCuit("20-10564397-8")).thenReturn(ProveedorFixture.proveedor());

        assertThat(new GetProveedorByCuitUseCaseImpl(proveedorGateway).getByCuit("20-10564397-8").proveedorId())
                .isEqualTo(8);
    }

    @Test
    void createDelegates() {
        var proveedor = ProveedorFixture.proveedor();
        when(proveedorGateway.create(proveedor)).thenReturn(proveedor);

        assertThat(new CreateProveedorUseCaseImpl(proveedorGateway).create(proveedor)).isSameAs(proveedor);
    }

    @Test
    void updateDelegates() {
        var proveedor = ProveedorFixture.proveedor();
        when(proveedorGateway.update(8, proveedor)).thenReturn(proveedor);

        assertThat(new UpdateProveedorUseCaseImpl(proveedorGateway).update(8, proveedor)).isSameAs(proveedor);
    }

    @Test
    void deleteDelegates() {
        new DeleteProveedorUseCaseImpl(proveedorGateway).delete(8);

        verify(proveedorGateway).delete(8);
    }
}
