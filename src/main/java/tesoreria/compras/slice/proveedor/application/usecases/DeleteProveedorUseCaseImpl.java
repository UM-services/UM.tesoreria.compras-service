package tesoreria.compras.slice.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.proveedor.domain.ports.in.DeleteProveedorUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.out.ProveedorGateway;

@Component
@RequiredArgsConstructor
public class DeleteProveedorUseCaseImpl implements DeleteProveedorUseCase {

    private final ProveedorGateway proveedorGateway;

    @Override
    public void delete(Integer proveedorId) {
        proveedorGateway.delete(proveedorId);
    }
}
