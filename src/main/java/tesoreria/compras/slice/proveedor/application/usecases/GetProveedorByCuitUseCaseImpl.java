package tesoreria.compras.slice.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.domain.ports.in.GetProveedorByCuitUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.out.ProveedorGateway;

@Component
@RequiredArgsConstructor
public class GetProveedorByCuitUseCaseImpl implements GetProveedorByCuitUseCase {

    private final ProveedorGateway proveedorGateway;

    @Override
    public Proveedor getByCuit(String cuit) {
        return proveedorGateway.getByCuit(cuit);
    }
}
