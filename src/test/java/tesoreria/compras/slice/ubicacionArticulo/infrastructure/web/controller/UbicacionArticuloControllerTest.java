package tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.ubicacionArticulo.UbicacionArticuloFixture;
import tesoreria.compras.slice.ubicacionArticulo.application.service.UbicacionArticuloService;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloRequest;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.mapper.UbicacionArticuloDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UbicacionArticuloControllerTest {

    @Mock
    private UbicacionArticuloService ubicacionArticuloService;

    private UbicacionArticuloController controller() {
        return new UbicacionArticuloController(ubicacionArticuloService, new UbicacionArticuloDtoMapper());
    }

    @Test
    void findByArticulo() {
        when(ubicacionArticuloService.getByArticulo(101L))
                .thenReturn(List.of(UbicacionArticuloFixture.ubicacionArticulo()));

        var response = controller().findByArticulo(101L);

        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().ubicacion().nombre()).isEqualTo("Rectorado");
        verify(ubicacionArticuloService).getByArticulo(101L);
    }

    @Test
    void save() {
        when(ubicacionArticuloService.save(any())).thenReturn(UbicacionArticuloFixture.ubicacionArticulo());

        var response = controller().save(new UbicacionArticuloRequest(1, 101L, new BigDecimal("20101090099")));

        assertThat(response.getBody().ubicacionArticuloId()).isEqualTo(50L);
    }
}
