package tesoreria.compras.ordencompra.domain.ports.in;

import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraItem;

import java.time.LocalDate;
import java.util.List;

public interface CreateOrdenCompraUseCase {

    OrdenCompra create(LocalDate fecha, Integer proveedorId, Integer sedeId, String observaciones,
                       List<OrdenCompraItem> items);
}
