package tesoreria.compras.slice.pedidoCompra.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.CreateCotizacionTramoUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.DeleteCotizacionTramoUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.ListCotizacionTramosUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.UpdateCotizacionTramoUseCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CotizacionTramoServiceTest {

    @Mock private ListCotizacionTramosUseCase listCotizacionTramosUseCase;
    @Mock private CreateCotizacionTramoUseCase createCotizacionTramoUseCase;
    @Mock private UpdateCotizacionTramoUseCase updateCotizacionTramoUseCase;
    @Mock private DeleteCotizacionTramoUseCase deleteCotizacionTramoUseCase;

    @InjectMocks
    private CotizacionTramoService service;

    private CotizacionTramo tramo() {
        return new CotizacionTramo(1L, "N1", 2, 1, false, (byte) 1);
    }

    @Test
    void listarDelega() {
        when(listCotizacionTramosUseCase.listar()).thenReturn(List.of(tramo()));

        assertThat(service.listar()).hasSize(1);
    }

    @Test
    void crearDelega() {
        CotizacionTramo t = tramo();
        when(createCotizacionTramoUseCase.crear(t)).thenReturn(t);

        assertThat(service.crear(t)).isEqualTo(t);
    }

    @Test
    void actualizarDelega() {
        CotizacionTramo t = tramo();
        when(updateCotizacionTramoUseCase.actualizar(1L, t)).thenReturn(t);

        assertThat(service.actualizar(1L, t)).isEqualTo(t);
    }

    @Test
    void eliminarDelega() {
        service.eliminar(1L);

        verify(deleteCotizacionTramoUseCase).eliminar(1L);
    }
}
