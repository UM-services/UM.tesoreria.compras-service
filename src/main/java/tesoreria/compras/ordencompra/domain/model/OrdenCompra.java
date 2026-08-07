package tesoreria.compras.ordencompra.domain.model;

import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInvalidaException;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraNoEditableException;
import tesoreria.compras.ordencompra.domain.exception.TransicionInvalidaException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrdenCompra(Long id, String numero, LocalDate fechaEmision, Integer proveedorId,
                          Integer sedeId, String observaciones, List<OrdenCompraItem> items,
                          OrdenCompraEstado estado) {

    public OrdenCompra {
        if (numero == null || fechaEmision == null || proveedorId == null || sedeId == null
                || items == null || items.isEmpty()) {
            throw new OrdenCompraInvalidaException("La orden requiere cabecera e ítems");
        }
        items = List.copyOf(items);
    }

    public static OrdenCompra nueva(String numero, LocalDate fechaEmision, Integer proveedorId, Integer sedeId,
                                    String observaciones, List<OrdenCompraItem> items) {
        return new OrdenCompra(null, numero, fechaEmision, proveedorId, sedeId, observaciones, items,
                OrdenCompraEstado.PENDIENTE_DE_APROBAR);
    }

    public BigDecimal total() {
        return items.stream().map(OrdenCompraItem::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public OrdenCompra actualizar(LocalDate fecha, Integer proveedor, Integer sede, String notas,
                                  List<OrdenCompraItem> nuevosItems) {
        if (estado != OrdenCompraEstado.PENDIENTE_DE_APROBAR) {
            throw new OrdenCompraNoEditableException(estado);
        }
        return new OrdenCompra(id, numero, fecha, proveedor, sede, notas, nuevosItems, estado);
    }

    public OrdenCompra transicionarA(OrdenCompraEstado destino) {
        if (!estado.permite(destino)) {
            throw new TransicionInvalidaException(estado, destino);
        }
        return new OrdenCompra(id, numero, fechaEmision, proveedorId, sedeId, observaciones, items, destino);
    }
}
