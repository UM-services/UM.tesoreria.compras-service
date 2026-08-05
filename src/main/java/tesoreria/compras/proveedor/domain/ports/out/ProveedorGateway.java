package tesoreria.compras.proveedor.domain.ports.out;

import tesoreria.compras.proveedor.domain.model.Proveedor;

public interface ProveedorGateway {

    Proveedor getProveedorById(Integer proveedorId);
}
