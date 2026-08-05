package tesoreria.compras.proveedor.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.proveedor.domain.model.Proveedor;
import tesoreria.compras.proveedor.domain.ports.in.GetProveedorByIdUseCase;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final GetProveedorByIdUseCase getProveedorByIdUseCase;

    public Proveedor getProveedorById(Integer proveedorId) {
        return getProveedorByIdUseCase.getProveedorById(proveedorId);
    }
}
