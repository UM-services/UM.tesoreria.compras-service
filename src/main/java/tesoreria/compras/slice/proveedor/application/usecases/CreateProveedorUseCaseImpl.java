package tesoreria.compras.slice.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.domain.ports.in.CreateProveedorUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.out.ProveedorGateway;

@Component
@RequiredArgsConstructor
public class CreateProveedorUseCaseImpl implements CreateProveedorUseCase {

    private final ProveedorGateway proveedorGateway;

    @Override
    public Proveedor create(Proveedor proveedor) {
        return proveedorGateway.create(proveedor);
    }
}
