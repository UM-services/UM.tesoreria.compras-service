package tesoreria.compras.slice.proveedor.domain.ports.in;

import tesoreria.compras.slice.proveedor.domain.model.Proveedor;

import java.util.List;

public interface SearchProveedoresUseCase {

    List<Proveedor> search(List<String> conditions);
}
