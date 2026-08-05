package tesoreria.compras.proveedor.domain.ports.in;

import tesoreria.compras.proveedor.domain.model.Proveedor;

public interface GetProveedorByIdUseCase {

    Proveedor getProveedorById(Integer proveedorId);
}
