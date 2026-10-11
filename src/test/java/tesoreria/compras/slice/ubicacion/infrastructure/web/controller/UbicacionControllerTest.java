package tesoreria.compras.slice.ubicacion.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.ubicacion.UbicacionFixture;
import tesoreria.compras.slice.ubicacion.application.service.UbicacionService;
import tesoreria.compras.slice.ubicacion.infrastructure.web.mapper.UbicacionDtoMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UbicacionControllerTest {

    @Mock
    private UbicacionService ubicacionService;

    @Test
    void findAll() {
        when(ubicacionService.getUbicaciones()).thenReturn(List.of(UbicacionFixture.ubicacion()));

        var response = new UbicacionController(ubicacionService, new UbicacionDtoMapper()).findAll();

        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().nombre()).isEqualTo("Rectorado");
        verify(ubicacionService).getUbicaciones();
    }
}
