package tesoreria.compras.ordencompra.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraItem;
import tesoreria.compras.ordencompra.domain.model.PaginaOrdenCompra;
import tesoreria.compras.ordencompra.domain.ports.in.CreateOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.in.GetOrdenCompraByNumeroUseCase;
import tesoreria.compras.ordencompra.domain.ports.in.GetOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.in.ListOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.in.TransitionOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.in.UpdateOrdenCompraUseCase;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenCompraService {

    private final CreateOrdenCompraUseCase createUseCase;
    private final GetOrdenCompraUseCase getUseCase;
    private final GetOrdenCompraByNumeroUseCase getByNumeroUseCase;
    private final ListOrdenCompraUseCase listUseCase;
    private final UpdateOrdenCompraUseCase updateUseCase;
    private final TransitionOrdenCompraUseCase transitionUseCase;

    public OrdenCompra create(LocalDate fecha, Integer proveedorId, Integer sedeId, String observaciones,
                              List<OrdenCompraItem> items) {
        return createUseCase.create(fecha, proveedorId, sedeId, observaciones, items);
    }

    public OrdenCompra get(Long id) {
        return getUseCase.get(id);
    }

    public OrdenCompra getByNumero(String numero) {
        return getByNumeroUseCase.getByNumero(numero);
    }

    public PaginaOrdenCompra list(OrdenCompraCriteria criteria) {
        return listUseCase.list(criteria);
    }

    public OrdenCompra update(Long id, LocalDate fecha, Integer proveedorId, Integer sedeId, String observaciones,
                              List<OrdenCompraItem> items) {
        return updateUseCase.update(id, fecha, proveedorId, sedeId, observaciones, items);
    }

    public OrdenCompra transition(Long id, OrdenCompraEstado estado) {
        return transitionUseCase.transition(id, estado);
    }
}
