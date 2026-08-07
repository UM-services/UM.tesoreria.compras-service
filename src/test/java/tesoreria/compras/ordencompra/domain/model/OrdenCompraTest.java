package tesoreria.compras.ordencompra.domain.model;

import org.junit.jupiter.api.Test;
import tesoreria.compras.ordencompra.OrdenCompraFixture;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInvalidaException;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraNoEditableException;
import tesoreria.compras.ordencompra.domain.exception.TransicionInvalidaException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrdenCompraTest {

    @Test
    void addsUpItemsAndWalksTheHappyPathOfTheStateMachine() {
        var orden = OrdenCompraFixture.pendiente();

        assertThat(orden.total()).isEqualByComparingTo("25.00");
        assertThat(orden.transicionarA(OrdenCompraEstado.APROBADA)
                .transicionarA(OrdenCompraEstado.ENVIADA)
                .transicionarA(OrdenCompraEstado.CUMPLIDA)
                .estado()).isEqualTo(OrdenCompraEstado.CUMPLIDA);
    }

    @Test
    void editsAPendingOrderWithoutTouchingItsNumberOrState() {
        var orden = OrdenCompraFixture.pendiente();

        var editada = orden.actualizar(orden.fechaEmision(), orden.proveedorId(), orden.sedeId(), "x",
                List.of(OrdenCompraFixture.item()));

        assertThat(editada.observaciones()).isEqualTo("x");
        assertThat(editada.numero()).isEqualTo(orden.numero());
        assertThat(editada.estado()).isEqualTo(OrdenCompraEstado.PENDIENTE_DE_APROBAR);
    }

    @Test
    void refusesToEditAnythingPastApproval() {
        var aprobada = OrdenCompraFixture.pendiente().transicionarA(OrdenCompraEstado.APROBADA);

        assertThatThrownBy(() -> aprobada.actualizar(aprobada.fechaEmision(), 8, 2, "x",
                List.of(OrdenCompraFixture.item())))
                .isInstanceOf(OrdenCompraNoEditableException.class)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rejectsInvalidItems() {
        assertThatThrownBy(() -> OrdenCompraItem.nuevo(null, "", BigDecimal.ONE, BigDecimal.ONE, 1L))
                .isInstanceOf(OrdenCompraInvalidaException.class)
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> OrdenCompraItem.nuevo(10, "", BigDecimal.ZERO, BigDecimal.ONE, 1L))
                .isInstanceOf(OrdenCompraInvalidaException.class);
        assertThatThrownBy(() -> OrdenCompraItem.nuevo(10, "", BigDecimal.ONE, new BigDecimal("-1"), 1L))
                .isInstanceOf(OrdenCompraInvalidaException.class);
    }

    @Test
    void rejectsAnOrderWithoutItems() {
        assertThatThrownBy(() -> OrdenCompra.nueva("OC-2026-000002", OrdenCompraFixture.FECHA, 8, 2, "", List.of()))
                .isInstanceOf(OrdenCompraInvalidaException.class);
    }

    @Test
    void closesTheStateGraphAtCumplidaAndAnulada() {
        assertThatThrownBy(() -> OrdenCompraFixture.pendiente().transicionarA(OrdenCompraEstado.ENVIADA))
                .isInstanceOf(TransicionInvalidaException.class);
        assertThat(OrdenCompraEstado.ENVIADA.permite(OrdenCompraEstado.FACTURA_PARCIAL)).isTrue();
        assertThat(OrdenCompraEstado.ENVIADA.permite(OrdenCompraEstado.CUMPLIDA_PARCIAL)).isTrue();
        assertThat(OrdenCompraEstado.ENVIADA.permite(OrdenCompraEstado.ANULADA)).isTrue();
        assertThat(OrdenCompraEstado.APROBADA.permite(OrdenCompraEstado.ANULADA)).isTrue();
        assertThat(OrdenCompraEstado.FACTURA_PARCIAL.permite(OrdenCompraEstado.CUMPLIDA)).isTrue();
        assertThat(OrdenCompraEstado.CUMPLIDA_PARCIAL.permite(OrdenCompraEstado.CUMPLIDA)).isTrue();
        assertThat(OrdenCompraEstado.CUMPLIDA.permite(OrdenCompraEstado.ANULADA)).isFalse();
        assertThat(OrdenCompraEstado.ANULADA.permite(OrdenCompraEstado.APROBADA)).isFalse();
    }

    @Test
    void keepsTheDefaultPageSizeInSyncWithTheStringTheControllerUses() {
        assertThat(Integer.parseInt(OrdenCompraCriteria.TAMANO_POR_DEFECTO_PARAM))
                .isEqualTo(OrdenCompraCriteria.TAMANO_POR_DEFECTO);
    }

    @Test
    void refusesAPageThatCouldNotBeDividedInto() {
        assertThatThrownBy(() -> new PaginaOrdenCompra(List.of(), 0, 0, 5L))
                .isInstanceOf(OrdenCompraInvalidaException.class);
    }
}
