package tesoreria.compras.slice.ubicacion.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.ubicacion.UbicacionFixture;
import tesoreria.compras.slice.ubicacion.domain.ports.in.GetUbicacionesUseCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UbicacionServiceTest {

    @Mock
    private GetUbicacionesUseCase getUbicacionesUseCase;

    @InjectMocks
    private UbicacionService ubicacionService;

    @Test
    void delegates() {
        when(getUbicacionesUseCase.getUbicaciones()).thenReturn(List.of(UbicacionFixture.ubicacion()));

        assertThat(ubicacionService.getUbicaciones()).hasSize(1);
    }
}
