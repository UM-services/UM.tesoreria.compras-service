package tesoreria.compras.ordencompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.model.PaginaOrdenCompra;
import tesoreria.compras.ordencompra.domain.ports.in.ListOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.out.OrdenCompraRepository;

@Component
@RequiredArgsConstructor
public class ListOrdenCompraUseCaseImpl implements ListOrdenCompraUseCase {

    private final OrdenCompraRepository repository;

    @Override
    public PaginaOrdenCompra list(OrdenCompraCriteria criteria) {
        return repository.findByCriteria(criteria);
    }
}
