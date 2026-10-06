package tesoreria.compras.slice.proveedor.domain.ports.out;

import tesoreria.compras.slice.proveedor.domain.model.Proveedor;

public interface ProveedorGateway {

    Proveedor getProveedorById(Integer proveedorId);
}
