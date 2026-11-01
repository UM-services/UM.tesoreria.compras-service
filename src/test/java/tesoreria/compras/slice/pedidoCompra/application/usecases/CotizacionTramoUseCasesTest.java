package tesoreria.compras.slice.pedidoCompra.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CotizacionGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CotizacionTramoUseCasesTest {

    @Mock
    private CotizacionGateway gateway;

    private CotizacionTramo tramo() {
        return new CotizacionTramo(1L, "N1", 2, 1, false, (byte) 1);
    }

    @Test
    void listarDelega() {
        when(gateway.listar()).thenReturn(List.of(tramo()));

        assertThat(new ListCotizacionTramosUseCaseImpl(gateway).listar()).hasSize(1);
    }

    @Test
    void crearDelega() {
        CotizacionTramo t = tramo();
        when(gateway.crear(t)).thenReturn(t);

        assertThat(new CreateCotizacionTramoUseCaseImpl(gateway).crear(t)).isEqualTo(t);
    }

    @Test
    void actualizarDelega() {
        CotizacionTramo t = tramo();
        when(gateway.actualizar(1L, t)).thenReturn(t);

        assertThat(new UpdateCotizacionTramoUseCaseImpl(gateway).actualizar(1L, t)).isEqualTo(t);
    }

    @Test
    void eliminarDelega() {
        new DeleteCotizacionTramoUseCaseImpl(gateway).eliminar(1L);

        verify(gateway).eliminar(1L);
    }
}
