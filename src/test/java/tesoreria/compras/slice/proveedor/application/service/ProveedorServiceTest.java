package tesoreria.compras.slice.proveedor.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.ProveedorFixture;
import tesoreria.compras.slice.proveedor.domain.ports.in.CreateProveedorUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.DeleteProveedorUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.GetPaginatedProveedoresUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.GetProveedorByCuitUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.GetProveedorByIdUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.SearchProveedoresUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.UpdateProveedorUseCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private GetProveedorByIdUseCase getProveedorByIdUseCase;

    @Mock
    private GetPaginatedProveedoresUseCase getPaginatedProveedoresUseCase;

    @Mock
    private SearchProveedoresUseCase searchProveedoresUseCase;

    @Mock
    private GetProveedorByCuitUseCase getProveedorByCuitUseCase;

    @Mock
    private CreateProveedorUseCase createProveedorUseCase;

    @Mock
    private UpdateProveedorUseCase updateProveedorUseCase;

    @Mock
    private DeleteProveedorUseCase deleteProveedorUseCase;

    @InjectMocks
    private ProveedorService proveedorService;

    @Test
    void delegatesToUseCase() {
        when(getProveedorByIdUseCase.getProveedorById(8)).thenReturn(ProveedorFixture.proveedor());

        var proveedor = proveedorService.getProveedorById(8);

        assertThat(proveedor.proveedorId()).isEqualTo(8);
        verify(getProveedorByIdUseCase).getProveedorById(8);
    }

    @Test
    void delegatesGetPaginated() {
        var page = new PaginatedResponse<>(List.of(ProveedorFixture.proveedor()), 1L, 1, 0, 20);
        when(getPaginatedProveedoresUseCase.getPaginated(0, 20)).thenReturn(page);

        assertThat(proveedorService.getPaginated(0, 20)).isSameAs(page);
    }

    @Test
    void delegatesSearch() {
        var conditions = List.of("acme");
        when(searchProveedoresUseCase.search(conditions)).thenReturn(List.of(ProveedorFixture.proveedor()));

        assertThat(proveedorService.search(conditions)).hasSize(1);
    }

    @Test
    void delegatesGetByCuit() {
        when(getProveedorByCuitUseCase.getByCuit("20-10564397-8")).thenReturn(ProveedorFixture.proveedor());

        assertThat(proveedorService.getByCuit("20-10564397-8").proveedorId()).isEqualTo(8);
    }

    @Test
    void delegatesCreate() {
        var proveedor = ProveedorFixture.proveedor();
        when(createProveedorUseCase.create(proveedor)).thenReturn(proveedor);

        assertThat(proveedorService.create(proveedor)).isSameAs(proveedor);
    }

    @Test
    void delegatesUpdate() {
        var proveedor = ProveedorFixture.proveedor();
        when(updateProveedorUseCase.update(8, proveedor)).thenReturn(proveedor);

        assertThat(proveedorService.update(8, proveedor)).isSameAs(proveedor);
    }

    @Test
    void delegatesDelete() {
        proveedorService.delete(8);

        verify(deleteProveedorUseCase).delete(8);
    }
}
