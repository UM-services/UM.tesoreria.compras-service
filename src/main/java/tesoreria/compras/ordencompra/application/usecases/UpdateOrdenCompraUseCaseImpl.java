package tesoreria.compras.ordencompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraNotFoundException;
import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraItem;
import tesoreria.compras.ordencompra.domain.ports.in.UpdateOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.out.OrdenCompraRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateOrdenCompraUseCaseImpl implements UpdateOrdenCompraUseCase {

    private final OrdenCompraRepository repository;

    @Override
    @Transactional
    public OrdenCompra update(Long id, LocalDate fecha, Integer proveedorId, Integer sedeId, String observaciones,
                              List<OrdenCompraItem> items) {
        OrdenCompra orden = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new OrdenCompraNotFoundException(id));
        return repository.save(orden.actualizar(fecha, proveedorId, sedeId, observaciones, items));
    }
}
