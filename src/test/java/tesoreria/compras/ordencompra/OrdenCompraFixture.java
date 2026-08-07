package tesoreria.compras.ordencompra;

import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class OrdenCompraFixture {

    public static final LocalDate FECHA = LocalDate.of(2026, 8, 5);
    public static final String NUMERO = "OC-2026-000001";

    private OrdenCompraFixture() {
    }

    public static OrdenCompraItem item() {
        return OrdenCompraItem.nuevo(10, "Papel", new BigDecimal("2"), new BigDecimal("12.50"), 20L);
    }

    public static OrdenCompraItem itemPersistido() {
        return new OrdenCompraItem(77L, 10, "Papel", new BigDecimal("2"), new BigDecimal("12.50"), 20L);
    }

    public static OrdenCompra pendiente() {
        return OrdenCompra.nueva(NUMERO, FECHA, 8, 2, "notas", List.of(item()));
    }

    public static OrdenCompra persistida() {
        return new OrdenCompra(1L, NUMERO, FECHA, 8, 2, "notas", List.of(itemPersistido()),
                OrdenCompraEstado.PENDIENTE_DE_APROBAR);
    }
}
