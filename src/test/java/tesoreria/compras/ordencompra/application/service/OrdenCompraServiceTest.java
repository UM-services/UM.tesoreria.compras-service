package tesoreria.compras.ordencompra.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.ordencompra.OrdenCompraFixture;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.domain.model.PaginaOrdenCompra;
import tesoreria.compras.ordencompra.domain.ports.in.CreateOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.in.GetOrdenCompraByNumeroUseCase;
import tesoreria.compras.ordencompra.domain.ports.in.GetOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.in.ListOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.in.TransitionOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.in.UpdateOrdenCompraUseCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenCompraServiceTest {

    @Mock
    private CreateOrdenCompraUseCase createUseCase;
    @Mock
    private GetOrdenCompraUseCase getUseCase;
    @Mock
    private GetOrdenCompraByNumeroUseCase getByNumeroUseCase;
    @Mock
    private ListOrdenCompraUseCase listUseCase;
    @Mock
    private UpdateOrdenCompraUseCase updateUseCase;
    @Mock
    private TransitionOrdenCompraUseCase transitionUseCase;

    @InjectMocks
    private OrdenCompraService service;

    @Test
    void delegatesEveryOperationToItsUseCase() {
        var orden = OrdenCompraFixture.persistida();
        var items = List.of(OrdenCompraFixture.item());
        var criteria = OrdenCompraCriteria.primeraPagina(null, null, null, null, null);
        var paginaEsperada = new PaginaOrdenCompra(List.of(orden), 0, 50, 1L);
        when(createUseCase.create(any(), any(), any(), any(), any())).thenReturn(orden);
        when(getUseCase.get(1L)).thenReturn(orden);
        when(getByNumeroUseCase.getByNumero(OrdenCompraFixture.NUMERO)).thenReturn(orden);
        when(listUseCase.list(criteria)).thenReturn(paginaEsperada);
        when(updateUseCase.update(any(), any(), any(), any(), any(), any())).thenReturn(orden);
        when(transitionUseCase.transition(1L, OrdenCompraEstado.APROBADA)).thenReturn(orden);

        assertThat(service.create(OrdenCompraFixture.FECHA, 8, 2, "notas", items)).isEqualTo(orden);
        assertThat(service.get(1L)).isEqualTo(orden);
        assertThat(service.getByNumero(OrdenCompraFixture.NUMERO)).isEqualTo(orden);
        assertThat(service.list(criteria)).isEqualTo(paginaEsperada);
        assertThat(service.update(1L, OrdenCompraFixture.FECHA, 8, 2, "notas", items)).isEqualTo(orden);
        assertThat(service.transition(1L, OrdenCompraEstado.APROBADA)).isEqualTo(orden);

        verify(createUseCase).create(OrdenCompraFixture.FECHA, 8, 2, "notas", items);
        verify(updateUseCase).update(1L, OrdenCompraFixture.FECHA, 8, 2, "notas", items);
        verify(transitionUseCase).transition(1L, OrdenCompraEstado.APROBADA);
    }
}
