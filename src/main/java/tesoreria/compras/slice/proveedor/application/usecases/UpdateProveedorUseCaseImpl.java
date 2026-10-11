package tesoreria.compras.slice.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.domain.ports.in.UpdateProveedorUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.out.ProveedorGateway;

@Component
@RequiredArgsConstructor
public class UpdateProveedorUseCaseImpl implements UpdateProveedorUseCase {

    private final ProveedorGateway proveedorGateway;

    @Override
    public Proveedor update(Integer proveedorId, Proveedor proveedor) {
        return proveedorGateway.update(proveedorId, proveedor);
    }
}
