package tesoreria.compras.slice.proveedor.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.domain.ports.in.CreateProveedorUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.DeleteProveedorUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.GetPaginatedProveedoresUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.GetProveedorByCuitUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.GetProveedorByIdUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.SearchProveedoresUseCase;
import tesoreria.compras.slice.proveedor.domain.ports.in.UpdateProveedorUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final GetProveedorByIdUseCase getProveedorByIdUseCase;
    private final GetPaginatedProveedoresUseCase getPaginatedProveedoresUseCase;
    private final SearchProveedoresUseCase searchProveedoresUseCase;
    private final GetProveedorByCuitUseCase getProveedorByCuitUseCase;
    private final CreateProveedorUseCase createProveedorUseCase;
    private final UpdateProveedorUseCase updateProveedorUseCase;
    private final DeleteProveedorUseCase deleteProveedorUseCase;

    public Proveedor getProveedorById(Integer proveedorId) {
        return getProveedorByIdUseCase.getProveedorById(proveedorId);
    }

    public PaginatedResponse<Proveedor> getPaginated(int page, int size) {
        return getPaginatedProveedoresUseCase.getPaginated(page, size);
    }

    public List<Proveedor> search(List<String> conditions) {
        return searchProveedoresUseCase.search(conditions);
    }

    public Proveedor getByCuit(String cuit) {
        return getProveedorByCuitUseCase.getByCuit(cuit);
    }

    public Proveedor create(Proveedor proveedor) {
        return createProveedorUseCase.create(proveedor);
    }

    public Proveedor update(Integer proveedorId, Proveedor proveedor) {
        return updateProveedorUseCase.update(proveedorId, proveedor);
    }

    public void delete(Integer proveedorId) {
        deleteProveedorUseCase.delete(proveedorId);
    }
}
