package tesoreria.compras.ordencompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraNotFoundException;
import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.domain.ports.in.TransitionOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.out.OrdenCompraRepository;

@Component
@RequiredArgsConstructor
public class TransitionOrdenCompraUseCaseImpl implements TransitionOrdenCompraUseCase {

    private final OrdenCompraRepository repository;

    @Override
    @Transactional
    public OrdenCompra transition(Long id, OrdenCompraEstado estado) {
        OrdenCompra orden = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new OrdenCompraNotFoundException(id));
        return repository.save(orden.transicionarA(estado));
    }
}
