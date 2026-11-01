package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.application.service.CotizacionTramoService;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionTramoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.CotizacionTramoDtoMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CotizacionTramoControllerTest {

    @Mock
    private CotizacionTramoService cotizacionTramoService;

    private CotizacionTramoController controller;

    @BeforeEach
    void setUp() {
        controller = new CotizacionTramoController(cotizacionTramoService, new CotizacionTramoDtoMapper());
    }

    private CotizacionTramo tramo(Integer tope, Integer cantidad) {
        return new CotizacionTramo(1L, "N1", tope, cantidad, tope == null, (byte) 1);
    }

    @Test
    void listarDevuelveLaLista() {
        when(cotizacionTramoService.listar()).thenReturn(List.of(tramo(2, 1)));

        var response = controller.listar();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).topeMultiplico()).isEqualTo(2);
    }

    @Test
    void crearDelega() {
        when(cotizacionTramoService.crear(any())).thenReturn(tramo(2, 1));

        var response = controller.crear(new CotizacionTramoRequest("N1", 2, 1, (byte) 1));

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().nombre()).isEqualTo("N1");
    }

    @Test
    void actualizarDelega() {
        when(cotizacionTramoService.actualizar(eq(1L), any())).thenReturn(tramo(6, 2));

        var response = controller.actualizar(1L, new CotizacionTramoRequest("N1", 6, 2, (byte) 1));

        assertThat(response.getBody().topeMultiplico()).isEqualTo(6);
        verify(cotizacionTramoService).actualizar(eq(1L), any());
    }

    @Test
    void eliminarDevuelve204() {
        var response = controller.eliminar(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(cotizacionTramoService).eliminar(1L);
    }
}
