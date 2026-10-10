package tesoreria.compras.slice.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.domain.ports.in.GetPaginatedProveedoresUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.out.ProveedorGateway;

@Component
@RequiredArgsConstructor
public class GetPaginatedProveedoresUseCaseImpl implements GetPaginatedProveedoresUseCase {

    private final ProveedorGateway proveedorGateway;

    @Override
    public PaginatedResponse<Proveedor> getPaginated(int page, int size) {
        return proveedorGateway.getPaginated(page, size);
    }
}
