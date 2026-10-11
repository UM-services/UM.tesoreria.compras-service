package tesoreria.compras.slice.proveedor.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.proveedor.ProveedorFixture;
import tesoreria.compras.slice.proveedor.domain.ports.out.ProveedorGateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProveedorByIdUseCaseImplTest {

    @Mock
    private ProveedorGateway proveedorGateway;

    @InjectMocks
    private GetProveedorByIdUseCaseImpl useCase;

    @Test
    void getsProveedorFromGateway() {
        when(proveedorGateway.getProveedorById(8)).thenReturn(ProveedorFixture.proveedor());

        var proveedor = useCase.getProveedorById(8);

        assertThat(proveedor.razonSocial()).isEqualTo("Proveedor de Prueba");
        verify(proveedorGateway).getProveedorById(8);
    }
}
