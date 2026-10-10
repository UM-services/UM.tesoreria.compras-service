package tesoreria.compras.slice.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.domain.ports.in.SearchProveedoresUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.out.ProveedorGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchProveedoresUseCaseImpl implements SearchProveedoresUseCase {

    private final ProveedorGateway proveedorGateway;

    @Override
    public List<Proveedor> search(List<String> conditions) {
        return proveedorGateway.search(conditions);
    }
}
