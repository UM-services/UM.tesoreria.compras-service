package tesoreria.compras.slice.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.domain.ports.in.GetProveedorByIdUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.out.ProveedorGateway;

@Component
@RequiredArgsConstructor
public class GetProveedorByIdUseCaseImpl implements GetProveedorByIdUseCase {

    private final ProveedorGateway proveedorGateway;

    @Override
    public Proveedor getProveedorById(Integer proveedorId) {
        return proveedorGateway.getProveedorById(proveedorId);
    }
}
