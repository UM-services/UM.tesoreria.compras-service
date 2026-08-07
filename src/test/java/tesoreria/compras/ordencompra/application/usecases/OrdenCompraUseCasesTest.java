package tesoreria.compras.ordencompra.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.ordencompra.OrdenCompraFixture;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraNotFoundException;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.domain.model.PaginaOrdenCompra;
import tesoreria.compras.ordencompra.domain.ports.out.OrdenCompraRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenCompraUseCasesTest {

    @Mock
    private OrdenCompraRepository repository;

    @InjectMocks
    private CreateOrdenCompraUseCaseImpl create;
    @InjectMocks
    private GetOrdenCompraUseCaseImpl get;
    @InjectMocks
    private GetOrdenCompraByNumeroUseCaseImpl getByNumero;
    @InjectMocks
    private ListOrdenCompraUseCaseImpl list;
    @InjectMocks
    private UpdateOrdenCompraUseCaseImpl update;
    @InjectMocks
    private TransitionOrdenCompraUseCaseImpl transition;

    @Test
    void stampsTheReservedCorrelativeOnTheNewOrder() {
        when(repository.reservarSiguienteNumero(2026)).thenReturn(1L);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var orden = create.create(OrdenCompraFixture.FECHA, 8, 2, "", List.of(OrdenCompraFixture.item()));

        assertThat(orden.numero()).isEqualTo("OC-2026-000001");
        assertThat(orden.estado()).isEqualTo(OrdenCompraEstado.PENDIENTE_DE_APROBAR);
        verify(repository).reservarSiguienteNumero(2026);
    }

    @Test
    void padsTheCorrelativeToSixDigitsAndTakesTheYearFromTheIssueDate() {
        when(repository.reservarSiguienteNumero(2027)).thenReturn(42L);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var orden = create.create(LocalDate.of(2027, 1, 2), 8, 2, "", List.of(OrdenCompraFixture.item()));

        assertThat(orden.numero()).isEqualTo("OC-2027-000042");
    }

    @Test
    void readsOrdersByIdByNumeroAndByCriteria() {
        var orden = OrdenCompraFixture.pendiente();
        when(repository.findById(1L)).thenReturn(Optional.of(orden));
        when(repository.findByNumero(orden.numero())).thenReturn(Optional.of(orden));
        when(repository.findByCriteria(any()))
                .thenReturn(new PaginaOrdenCompra(List.of(orden), 0, 50, 1L));

        assertThat(get.get(1L)).isEqualTo(orden);
        assertThat(getByNumero.getByNumero(orden.numero())).isEqualTo(orden);
        assertThat(list.list(OrdenCompraCriteria.primeraPagina(null, null, null, null, null)).contenido())
                .containsExactly(orden);
    }

    @Test
    void takesTheWriteLockBeforeEditingOrTransitioning() {
        var orden = OrdenCompraFixture.pendiente();
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(orden));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(update.update(1L, orden.fechaEmision(), 8, 2, "x", List.of(OrdenCompraFixture.item()))
                .observaciones()).isEqualTo("x");
        assertThat(transition.transition(1L, OrdenCompraEstado.APROBADA).estado())
                .isEqualTo(OrdenCompraEstado.APROBADA);

        verify(repository, never()).findById(1L);
    }

    @Test
    void reportsAMissingOrderInsteadOfWritingAnything() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(repository.findByNumero("OC-2026-999999")).thenReturn(Optional.empty());
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> get.get(1L)).isInstanceOf(OrdenCompraNotFoundException.class);
        assertThatThrownBy(() -> getByNumero.getByNumero("OC-2026-999999"))
                .isInstanceOf(OrdenCompraNotFoundException.class);
        assertThatThrownBy(() -> update.update(1L, LocalDate.now(), 1, 1, "", List.of(OrdenCompraFixture.item())))
                .isInstanceOf(OrdenCompraNotFoundException.class);
        assertThatThrownBy(() -> transition.transition(1L, OrdenCompraEstado.APROBADA))
                .isInstanceOf(OrdenCompraNotFoundException.class);

        verify(repository, never()).save(any());
    }
}
