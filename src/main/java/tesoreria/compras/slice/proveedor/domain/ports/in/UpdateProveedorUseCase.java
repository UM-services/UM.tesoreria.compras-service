package tesoreria.compras.slice.proveedor.domain.ports.in;

import tesoreria.compras.slice.proveedor.domain.model.Proveedor;

public interface UpdateProveedorUseCase {

    Proveedor update(Integer proveedorId, Proveedor proveedor);
}
