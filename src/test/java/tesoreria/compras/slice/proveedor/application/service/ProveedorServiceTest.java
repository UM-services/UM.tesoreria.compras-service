package tesoreria.compras.slice.proveedor.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.proveedor.ProveedorFixture;
import tesoreria.compras.slice.proveedor.domain.ports.in.GetProveedorByIdUseCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private GetProveedorByIdUseCase getProveedorByIdUseCase;

    @InjectMocks
    private ProveedorService proveedorService;

    @Test
    void delegatesToUseCase() {
        when(getProveedorByIdUseCase.getProveedorById(8)).thenReturn(ProveedorFixture.proveedor());

        var proveedor = proveedorService.getProveedorById(8);

        assertThat(proveedor.proveedorId()).isEqualTo(8);
        verify(getProveedorByIdUseCase).getProveedorById(8);
    }
}
