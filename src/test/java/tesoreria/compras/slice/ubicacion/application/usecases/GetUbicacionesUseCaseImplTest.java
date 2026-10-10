package tesoreria.compras.slice.ubicacion.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.ubicacion.UbicacionFixture;
import tesoreria.compras.slice.ubicacion.domain.ports.out.UbicacionGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUbicacionesUseCaseImplTest {

    @Mock
    private UbicacionGateway ubicacionGateway;

    @Test
    void delegates() {
        when(ubicacionGateway.getUbicaciones()).thenReturn(List.of(UbicacionFixture.ubicacion()));

        var result = new GetUbicacionesUseCaseImpl(ubicacionGateway).getUbicaciones();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().nombre()).isEqualTo("Rectorado");
        verify(ubicacionGateway).getUbicaciones();
    }
}
