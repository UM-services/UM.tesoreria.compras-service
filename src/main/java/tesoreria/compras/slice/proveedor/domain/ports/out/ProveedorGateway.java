package tesoreria.compras.slice.proveedor.domain.ports.out;

import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;

import java.util.List;

public interface ProveedorGateway {

    Proveedor getProveedorById(Integer proveedorId);

    PaginatedResponse<Proveedor> getPaginated(int page, int size);

    List<Proveedor> search(List<String> conditions);

    Proveedor getByCuit(String cuit);

    Proveedor create(Proveedor proveedor);

    Proveedor update(Integer proveedorId, Proveedor proveedor);

    void delete(Integer proveedorId);
}
