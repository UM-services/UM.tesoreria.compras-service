package tesoreria.compras.slice.proveedor.domain.ports.in;

import tesoreria.compras.slice.proveedor.domain.model.Proveedor;

public interface CreateProveedorUseCase {

    Proveedor create(Proveedor proveedor);
}
